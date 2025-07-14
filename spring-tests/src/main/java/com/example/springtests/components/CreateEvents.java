package com.example.springtests.components;

import com.example.springtests.models.MarketTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CreateEvents {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Random random = new Random();
    private static final Map<String, Integer> STATUS_NAME_TO_CODE = Map.of(
            "active", 0,
            "suspended", 1,
            "disabled", 2,
            "win", 3,
            "loss", 4,
            "return", 5,
            "half_win", 6,
            "half_loss", 7,
            "cancelled", 8
    );
    private static final List<String> STATUS_NAMES = new ArrayList<>(STATUS_NAME_TO_CODE.keySet());


    private static final List<MarketTemplate> MARKET_TEMPLATES = loadMarketTemplates();
    private static List<MarketTemplate> loadMarketTemplates() {
        try (InputStream is = CreateEvents.class.getClassLoader().getResourceAsStream("markets.json")) {
            if (is == null) {
                throw new IllegalStateException("Не найден файл markets.json в resources");
            }
            return OBJECT_MAPPER.readValue(is, new TypeReference<List<MarketTemplate>>() {});
        } catch (IOException e) {
            throw new UncheckedIOException("Ошибка при загрузке market templates из markets.json", e);
        }
    }

    public static String createMarketEvent(String reportId) {
        return createMarketEventWithStatus(reportId, null, false);
    }

    public static String createMarketEventWithStatus(String eventId, String status, Boolean isFinal) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("id", eventId);
        event.put("message_type", "market_event");
        if (status != null) event.put("status", status);
        else event.put("status", randomStatus());

        List<Map<String, Object>> markets = MARKET_TEMPLATES.stream()
                .limit(1)
                .map(marketDef -> {
                    Map<String, Object> market = new LinkedHashMap<>();
                    market.put("market_type_id", marketDef.getMarket_type_id());
                    market.put("specifiers", List.of(Map.of(
                            "name", randomString(5),
                            "value", round(randomDouble(0.1, 10.0), 3)
                    )));

                    List<Map<String, Object>> selections = marketDef.getSelections_ids().stream()
                            .map(selId -> {
                                Map<String, Object> selection = new LinkedHashMap<>();
                                selection.put("selection_type_id", selId);
                                selection.put("status", STATUS_NAME_TO_CODE.get(status));

                                if (!isFinal) {
                                    Map<String, Object> odds = new HashMap<>();
                                    odds.put("price", round(randomDouble(1.5, 10.5), 2));
                                    odds.put("probability", round(randomDouble(0.01, 0.99), 5));
                                    selection.put("odds", odds);
                                }

                                return selection;
                            })
                            .collect(Collectors.toList());

                    market.put("selections", selections);
                    return market;
                })
                .collect(Collectors.toList());

        event.put("markets", markets);

        return toJson(event);
    }

    public static String createMarketReport(String reportId) {
        return createMarketReportWithStatus(reportId, null);
    }

    public static String createMarketReportWithStatus(String eventId, String forcedStatus) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("id", eventId);
        report.put("message_type", "market_report");

        List<Map<String, Object>> markets = MARKET_TEMPLATES.stream()
                .limit(1)
                .map(marketDef -> {
                    Map<String, Object> market = new LinkedHashMap<>();
                    market.put("market_type_id", marketDef.getMarket_type_id());

                    List<Map<String, Object>> selections = marketDef.getSelections_ids().stream()
                            .map(selId -> {
                                Map<String, Object> selection = new LinkedHashMap<>();
                                selection.put("selection_type_id", selId);
                                selection.put("status", forcedStatus != null ? forcedStatus : randomStatus());
                                return selection;
                            })
                            .collect(Collectors.toList());

                    market.put("selections", selections);
                    return market;
                })
                .collect(Collectors.toList());

        report.put("markets", markets);

        return toJson(report);
    }

    private static double randomDouble(double min, double max) {
        return Math.round((min + (max - min) * ThreadLocalRandom.current().nextDouble()) * 100000.0) / 100000.0;
    }


    private static String randomStatus() {
        int index = random.nextInt(STATUS_NAMES.size());
        return STATUS_NAMES.get(index);
    }

    private static String randomString(int length) {
        return ThreadLocalRandom.current().ints('a', 'z' + 1)
                .limit(length)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }
    private static double round(double value, int precision) {
        return BigDecimal.valueOf(value)
                .setScale(precision, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }

    public static String createInvalidJson() {
        return "{\"id\": 123456789, \"markets\": [";
    }
}