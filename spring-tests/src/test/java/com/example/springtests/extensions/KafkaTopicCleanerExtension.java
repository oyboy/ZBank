package com.example.springtests.extensions;

import com.example.springtests.kafka.KafkaService;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class KafkaTopicCleanerExtension implements BeforeEachCallback {
    private final KafkaService kafkaService = new KafkaService();

    @Override
    public void beforeEach(ExtensionContext context) {
        kafkaService.clearTopic("markets");
        kafkaService.clearTopic("processed_markets");
    }
}
