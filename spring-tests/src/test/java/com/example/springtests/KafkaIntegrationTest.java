package com.example.springtests;

import com.example.springtests.components.CreateEvents;
import com.example.springtests.configuration.KafkaConfig;
import com.example.springtests.models.MarketDataRecord;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class KafkaIntegrationTest {
    private static final String INPUT_TOPIC = "markets";
    private static final String OUTPUT_TOPIC = "processed_markets";

    private static final String TEST_EVENT_ID = "123456789";
    private static final String TEST_REPORT_ID = "67890";

    private static final String POSTGRES_JDBC_URL = "jdbc:postgresql://localhost:5433/mydatabase";
    private static final String POSTGRES_USER = "user";
    private static final String POSTGRES_PASSWORD = "password";

    private static Jdbi jdbi;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    @BeforeAll
    static void beforeAll() {
        jdbi = Jdbi.create(POSTGRES_JDBC_URL, POSTGRES_USER, POSTGRES_PASSWORD);
        jdbi.registerRowMapper(BeanMapper.factory(MarketDataRecord.class));
    }

    @BeforeEach
    public void setUp() {
        producer = new KafkaProducer<>(KafkaConfig.getProducerConfig());
        consumer = new KafkaConsumer<>(KafkaConfig.getConsumerConfig());

        try {
            jdbi.useHandle(handle -> handle.execute("TRUNCATE TABLE market_data RESTART IDENTITY"));
        } catch (Exception e) {
            fail("Не удалось очистить таблицу market_data. Убедитесь, что docker-compose запущен и сервис 'app' успешно применил миграции.", e);
        }
    }

    @AfterEach
    void tearDown() {
        if (producer != null) producer.close();
        if (consumer != null) consumer.close();
    }

    @Test
    void shouldProcessMarketEventAndVerifyDatabaseAndOutputTopic() throws ExecutionException, InterruptedException, TimeoutException {
        String testEventJson = CreateEvents.createTestEventJson();

        producer.send(new ProducerRecord<>(INPUT_TOPIC, TEST_EVENT_ID, testEventJson)).get(10, SECONDS);

        await().atMost(15, SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    List<MarketDataRecord> records = jdbi.withHandle(handle ->
                            handle.createQuery("SELECT id, event_id, market_type_id, selection_type_id, price, probability, status FROM market_data WHERE event_id = :eventId::bigint ORDER BY selection_type_id")
                                    .bind("eventId", TEST_EVENT_ID)
                                    .mapTo(MarketDataRecord.class)
                                    .list()
                    );

                    assertEquals(2, records.size(), "Ожидалось 2 записи в БД, но было найдено: " + records.size());

                    MarketDataRecord firstRecord = records.get(0);
                    assertEquals(TEST_EVENT_ID, firstRecord.getEventId());
                    assertEquals(1L, firstRecord.getMarketTypeId());
                    assertEquals(1L, firstRecord.getSelectionTypeId());
                    assertEquals(2.4, firstRecord.getPrice());
                    assertEquals(1.55555, firstRecord.getProbability());
                    assertEquals("suspended", firstRecord.getStatus());

                    MarketDataRecord secondRecord = records.get(1);
                    assertEquals(TEST_EVENT_ID, secondRecord.getEventId());
                    assertEquals(1L, secondRecord.getMarketTypeId());
                    assertEquals(2L, secondRecord.getSelectionTypeId());
                    assertEquals(4.7, secondRecord.getPrice());
                    assertEquals(2.8888, secondRecord.getProbability());
                    assertEquals("suspended", secondRecord.getStatus());
                });


        consumer.subscribe(Collections.singletonList(OUTPUT_TOPIC));

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        List<ConsumerRecord<String, String>> actualMessages = new ArrayList<>();
        records.forEach(actualMessages::add);

        assertFalse(records.isEmpty(), "Не получено ни одного сообщения из топика 'processed_markets' за 10 секунд");

        ConsumerRecord<String, String> receivedRecord = actualMessages.get(actualMessages.size() - 1);

        assertEquals(TEST_EVENT_ID, receivedRecord.key());
        assertEquals(""" 
                {"id":123456789,"is_success":true,"unique_markets_ids":[1],"unique_selection_ids":[1,2]}""", receivedRecord.value());
    }

    @Test
    void shouldProcessMarketReportAndVerifyDatabaseAndOutputTopic() throws Exception {
        String testReportJson = CreateEvents.createTestReportJson();
        producer.send(new ProducerRecord<>(INPUT_TOPIC, TEST_REPORT_ID, testReportJson)).get(10, SECONDS);

        await().atMost(15, SECONDS)
                .untilAsserted(() -> {
                    List<MarketDataRecord> records = jdbi.withHandle(handle ->
                            handle.createQuery("SELECT * FROM market_data WHERE event_id = :reportId::bigint ORDER BY selection_type_id")
                                    .bind("reportId", TEST_REPORT_ID)
                                    .mapTo(MarketDataRecord.class)
                                    .list());

                    assertEquals(2, records.size(), "Ожидалось 2 записи в БД, но было найдено: " + records.size());

                    MarketDataRecord oddRecord = records.get(0);
                    assertEquals(2.5 + 201, oddRecord.getPrice());
                    assertEquals(0.555 + (201 / 10.0), oddRecord.getProbability());
                    assertEquals("active", oddRecord.getStatus());

                    MarketDataRecord evenRecord = records.get(1);
                    assertEquals(1.5 + 202, evenRecord.getPrice());
                    assertEquals(0.445 + (202 / 10.0), evenRecord.getProbability());
                    assertEquals("suspended", evenRecord.getStatus());
                });

        consumer.subscribe(Collections.singletonList(OUTPUT_TOPIC));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        assertFalse(records.isEmpty());

        List<ConsumerRecord<String, String>> recordList = new ArrayList<>();
        records.forEach(recordList::add);
        ConsumerRecord<String, String> receivedRecord = recordList.stream()
                .filter(r -> TEST_REPORT_ID.equals(r.key()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найдено сообщение с нужным ключом"));

        assertTrue(receivedRecord.value().contains("\"processed_markets_ids\":[2]"));
        assertTrue(receivedRecord.value().contains("\"processed_selections_ids\":[201,202]"));
    }

    @Test
    void shouldSendErrorMessageWhenInvalidJsonReceived() throws Exception {
        String invalidJson = CreateEvents.createInvalidJson();
        producer.send(new ProducerRecord<>(INPUT_TOPIC, TEST_EVENT_ID, invalidJson)).get(10, SECONDS);

        await().atMost(15, SECONDS)
                .untilAsserted(() -> {
                    List<MarketDataRecord> records = jdbi.withHandle(handle ->
                            handle.createQuery("SELECT id, event_id, market_type_id, selection_type_id, price, probability, status FROM market_data WHERE event_id = :eventId::bigint ORDER BY selection_type_id")
                                    .bind("eventId", TEST_EVENT_ID)
                                    .mapTo(MarketDataRecord.class)
                                    .list()
                    );

                    assertEquals(0, records.size(), "Ожидалось 0 записей в БД, но было найдено: " + records.size());
                });

        consumer.subscribe(Collections.singletonList(OUTPUT_TOPIC));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        assertFalse(records.isEmpty(), "Не получено сообщение об ошибке");

        List<ConsumerRecord<String, String>> actualMessages = new ArrayList<>();
        records.forEach(actualMessages::add);
        ConsumerRecord<String, String> errorRecord = actualMessages.get(actualMessages.size() - 1);

        assertEquals(TEST_EVENT_ID, errorRecord.key(), "Ключ сообщения должен быть равен TEST_EVENT_ID");

        JsonNode errorMessage = new ObjectMapper().readTree(errorRecord.value());
        assertEquals(-922337203685477580L, errorMessage.get("id").asLong(), "Поле id в сообщении об ошибке");
        assertFalse(errorMessage.get("is_success").asBoolean(), "Поле is_success должно быть false");
        assertEquals("Deserialization error. Received message with wrong format.",
                errorMessage.get("error_description").asText(),
                "Неверное описание ошибки");

        List<MarketDataRecord> dbRecords = jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM market_data WHERE event_id = :eventId::bigint")
                        .bind("eventId", TEST_EVENT_ID)
                        .mapTo(MarketDataRecord.class)
                        .list());
        assertTrue(dbRecords.isEmpty(),
                "При ошибке десериализации в БД не должно быть записей");
    }
}