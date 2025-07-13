package com.example.springtests;

import com.example.springtests.components.CreateEvents;
import com.example.springtests.kafka.KafkaService;
import com.example.springtests.models.MarketDataRecord;
import com.example.springtests.repositories.MarketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.*;

public class KafkaIntegrationTest {
    private static final String INPUT_TOPIC = "markets";
    private static final String OUTPUT_TOPIC = "processed_markets";

    private static final String TEST_EVENT_ID = "123456789";
    private static final String TEST_REPORT_ID = "67890";

    private static final Integer MAX_MESSAGES = 3;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private KafkaService kafkaService;

    @BeforeEach
    public void setUp() {
        kafkaService = new KafkaService();
        MarketRepository marketRepository = new MarketRepository();

        try {
            marketRepository.clearMarketData();
        } catch (Exception e) {
            fail("Ошибка при очистке БД", e);
        }
    }

    @AfterEach
    void tearDown() {
        kafkaService.close();
    }

    @Test
    void shouldProcessMarketEventAndVerifyDatabaseAndOutputTopic() throws ExecutionException, InterruptedException, TimeoutException {
        String testEventJson = CreateEvents.createTestEventJson();
        kafkaService.sendMessage(INPUT_TOPIC, TEST_EVENT_ID, testEventJson);

        List<MarketDataRecord> records = kafkaService.awaitMarketDataInserted(TEST_EVENT_ID, 2, TIMEOUT);
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

        List<ConsumerRecord<String, String>> recivedMessages = kafkaService.pollMessages(OUTPUT_TOPIC, MAX_MESSAGES, TIMEOUT);
        assertFalse(records.isEmpty(), "Не получено ни одного сообщения из топика 'processed_markets'");

        ConsumerRecord<String, String> last = recivedMessages.getLast();
        assertEquals(TEST_EVENT_ID, last.key());
        assertEquals(""" 
                {"id":123456789,"is_success":true,"unique_markets_ids":[1],"unique_selection_ids":[1,2]}""", last.value());
    }

    @Test
    void shouldProcessMarketReportAndVerifyDatabaseAndOutputTopic() throws Exception {
        String testReportJson = CreateEvents.createTestReportJson();
        kafkaService.sendMessage(INPUT_TOPIC, TEST_REPORT_ID, testReportJson);


        List<MarketDataRecord> records = kafkaService.awaitMarketDataInserted(TEST_REPORT_ID, 2, TIMEOUT);
        MarketDataRecord oddRecord = records.get(0);
        assertEquals(2.5 + 201, oddRecord.getPrice());
        assertEquals(0.555 + (201 / 10.0), oddRecord.getProbability());
        assertEquals("active", oddRecord.getStatus());

        MarketDataRecord evenRecord = records.get(1);
        assertEquals(1.5 + 202, evenRecord.getPrice());
        assertEquals(0.445 + (202 / 10.0), evenRecord.getProbability());
        assertEquals("suspended", evenRecord.getStatus());

        List<ConsumerRecord<String, String>> recordList = kafkaService.pollMessages(OUTPUT_TOPIC, MAX_MESSAGES, TIMEOUT);
        assertFalse(recordList.isEmpty(), "Не получено ни одного сообщения из топика 'processed_markets'");

        ConsumerRecord<String, String> last = recordList.getLast();
        assertTrue(last.value().contains("\"processed_markets_ids\":[2]"));
        assertTrue(last.value().contains("\"processed_selections_ids\":[201,202]"));
    }

    @Test
    void shouldSendErrorMessageWhenInvalidJsonReceived() throws Exception {
        String invalidJson = CreateEvents.createInvalidJson();
        kafkaService.sendMessage(INPUT_TOPIC, TEST_EVENT_ID, invalidJson);

        kafkaService.awaitMarketDataInserted(TEST_EVENT_ID, 0, TIMEOUT);

        List<ConsumerRecord<String, String>> records = kafkaService.pollMessages(OUTPUT_TOPIC, MAX_MESSAGES, TIMEOUT);
        assertFalse(records.isEmpty());
        ConsumerRecord<String, String> errorRecord = records.getLast();

        assertEquals(TEST_EVENT_ID, errorRecord.key(), "Ключ сообщения должен быть равен TEST_EVENT_ID");

        JsonNode errorMessage = new ObjectMapper().readTree(errorRecord.value());
        assertEquals(-922337203685477580L, errorMessage.get("id").asLong(), "Поле id в сообщении об ошибке");
        assertFalse(errorMessage.get("is_success").asBoolean(), "Поле is_success должно быть false");
        assertEquals("Deserialization error. Received message with wrong format.",
                errorMessage.get("error_description").asText(),
                "Неверное описание ошибки");
    }
}