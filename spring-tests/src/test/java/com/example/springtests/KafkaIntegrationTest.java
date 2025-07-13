package com.example.springtests;

import com.example.springtests.components.CreateEvents;
import com.example.springtests.components.ExpectedResultGenerator;
import com.example.springtests.kafka.KafkaService;
import com.example.springtests.models.MarketDataRecord;
import com.example.springtests.repositories.MarketRepository;
import com.fasterxml.jackson.core.type.TypeReference;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaIntegrationTest {
    private static final String INPUT_TOPIC = "markets";
    private static final String OUTPUT_TOPIC = "processed_markets";

    private static final String TEST_EVENT_ID = "123456789";
    private static final String TEST_REPORT_ID = "67890";

    private static final Integer MAX_MESSAGES = 3;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private final ObjectMapper objectMapper = new ObjectMapper();
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
    void shouldProcessMarketEventAndVerifyDatabaseAndOutputTopic() throws Exception {
        String eventJson = CreateEvents.createMarketEvent(TEST_EVENT_ID);
        kafkaService.sendMessage(INPUT_TOPIC, TEST_EVENT_ID, eventJson);

        List<MarketDataRecord> actualRecords = kafkaService.awaitMarketDataInserted(TEST_EVENT_ID, 2, TIMEOUT);
        assertFalse(actualRecords.isEmpty(), "Записи в БД не найдены");

        Map<String, Object> eventMap = objectMapper.readValue(eventJson, new TypeReference<>() {});

        List<MarketDataRecord> expectedRecords = ExpectedResultGenerator.generateExpectedRecordsFromEvent(eventMap);

        String expectedJson = objectMapper.writeValueAsString(expectedRecords);
        String actualJson = objectMapper.writeValueAsString(actualRecords);
        assertThatJson(actualJson)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(expectedJson);

        List<ConsumerRecord<String, String>> kafkaMessages = kafkaService.pollMessages(OUTPUT_TOPIC, 10, TIMEOUT);
        assertFalse(kafkaMessages.isEmpty(), "Нет сообщений из топика processed_markets");
        ConsumerRecord<String, String> lastMessage = kafkaMessages.getLast();

        Map<String, Object> expectedKafkaMessageMap = ExpectedResultGenerator.generateExpectedKafkaEventMessage(eventMap);
        String expectedKafkaMessageJson = objectMapper.writeValueAsString(expectedKafkaMessageMap);
        assertThatJson(lastMessage.value()).isEqualTo(expectedKafkaMessageJson);
    }


    @Test
    void shouldProcessMarketReportAndVerifyDatabaseAndOutputTopic() throws Exception {
        String testReportJson = CreateEvents.createMarketReport(TEST_REPORT_ID);
        kafkaService.sendMessage(INPUT_TOPIC, TEST_REPORT_ID, testReportJson);

        List<MarketDataRecord> actualRecords = kafkaService.awaitMarketDataInserted(TEST_REPORT_ID, 2, TIMEOUT);
        assertFalse(actualRecords.isEmpty(), "Записи в БД не найдены");

        Map<String, Object> reportMap = objectMapper.readValue(testReportJson, new TypeReference<>() {});
        List<MarketDataRecord> expectedRecords = ExpectedResultGenerator.generateExpectedRecordsFromReport(reportMap);

        String expectedJson = objectMapper.writeValueAsString(expectedRecords);
        String actualJson = objectMapper.writeValueAsString(actualRecords);
        assertThatJson(actualJson)
                .whenIgnoringPaths("[*].id")
                .isEqualTo(expectedJson);

        List<ConsumerRecord<String, String>> kafkaMessages = kafkaService.pollMessages(OUTPUT_TOPIC, MAX_MESSAGES, TIMEOUT);
        assertFalse(kafkaMessages.isEmpty(), "Не получено ни одного сообщения из топика 'processed_markets'");
        ConsumerRecord<String, String> last = kafkaMessages.getLast();
        assertEquals(TEST_REPORT_ID, last.key());

        Map<String, Object> expectedKafkaMessage = ExpectedResultGenerator.generateExpectedKafkaReportMessage(reportMap);
        String expectedKafkaMessageJson = objectMapper.writeValueAsString(expectedKafkaMessage);

        assertThatJson(last.value()).isEqualTo(expectedKafkaMessageJson);
    }


    @Test
    void shouldSendErrorMessageWhenInvalidJsonReceived() throws Exception {
        String invalidJson = CreateEvents.createInvalidJson();
        kafkaService.sendMessage(INPUT_TOPIC, TEST_EVENT_ID, invalidJson);

        kafkaService.awaitMarketDataInserted(TEST_EVENT_ID, 0, TIMEOUT);

        List<ConsumerRecord<String, String>> records = kafkaService.pollMessages(OUTPUT_TOPIC, MAX_MESSAGES, TIMEOUT);
        assertFalse(records.isEmpty(), "Не получено сообщение об ошибке");

        ConsumerRecord<String, String> errorRecord = records.getLast();
        //assertEquals(TEST_EVENT_ID, errorRecord.key(), "Ключ сообщения должен быть равен TEST_EVENT_ID");

        Map<String, Object> expectedError = ExpectedResultGenerator.generateExpectedErrorMessage(TEST_EVENT_ID);
        assertThatJson(errorRecord.value()).isEqualTo(expectedError);
    }
}