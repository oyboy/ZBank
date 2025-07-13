package com.example.springtests.kafka;

import com.example.springtests.models.MarketDataRecord;
import com.example.springtests.repositories.MarketRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class KafkaService {
    private final MarketRepository marketRepository;
    private final KafkaProducer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;

    public KafkaService() {
        this.producer = new KafkaProducer<>(KafkaConfig.getProducerConfig());
        this.consumer = new KafkaConsumer<>(KafkaConfig.getConsumerConfig());
        this.marketRepository = new MarketRepository();
    }

    public void sendMessage(String topic, String key, String value) throws ExecutionException, InterruptedException, TimeoutException {
        producer.send(new ProducerRecord<>(topic, key, value)).get(10, TimeUnit.SECONDS);
    }

    public List<ConsumerRecord<String, String>> pollMessages(String topic, int maxMessages, Duration timeout) {
        consumer.subscribe(Collections.singletonList(topic));
        List<ConsumerRecord<String, String>> collected = new ArrayList<>();
        long end = System.currentTimeMillis() + timeout.toMillis();

        while (System.currentTimeMillis() < end && collected.size() < maxMessages) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            records.forEach(collected::add);
        }
        return collected;
    }

    public List<MarketDataRecord> awaitMarketDataInserted(String eventId, int expectedCount, Duration timeout) {
        Awaitility.await()
                .untilAsserted(() -> {
                    List<MarketDataRecord> records = marketRepository.getMarketDataByEventId(eventId);
                    if (records.size() != expectedCount) {
                        throw new AssertionError("Ожидалось " + expectedCount + " записей, но было найдено " + records.size());
                    }
                });
        return marketRepository.getMarketDataByEventId(eventId);
    }

    public void close() {
        producer.close();
        consumer.close();
    }
}
