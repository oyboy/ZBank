package com.example.springtests.components;

import com.example.springtests.models.MarketDataRecord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ExpectedResultGenerator {
    private static final Set<String> FINAL_STATUSES = Set.of(
            "disabled", "win", "loss", "return", "half_win", "half_loss", "cancelled"
    );

    public static List<MarketDataRecord> generateExpectedRecordsFromEvent(Map<String, Object> event) {
        String eventId = event.get("id").toString();
        List<Map<String, Object>> markets = (List<Map<String, Object>>) event.get("markets");

        List<MarketDataRecord> records = new ArrayList<>();

        for (Map<String, Object> market : markets) {
            Long marketTypeId = ((Number) market.get("market_type_id")).longValue();
            List<Map<String, Object>> selections = (List<Map<String, Object>>) market.get("selections");

            for (Map<String, Object> sel : selections) {
                Long selectionTypeId = ((Number) sel.get("selection_type_id")).longValue();
                int statusCode = ((Number) sel.get("status")).intValue();
                String status = statusFromCode(statusCode);

                MarketDataRecord rec = new MarketDataRecord();
                rec.setEventId(eventId);
                rec.setMarketTypeId(marketTypeId);
                rec.setSelectionTypeId(selectionTypeId);
                rec.setStatus(status);

                if (!FINAL_STATUSES.contains(status)) {
                    Map<String, Object> odds = (Map<String, Object>) sel.get("odds");
                    rec.setPrice(round(((Number) odds.get("price")).doubleValue(), 2));
                    rec.setProbability(round(((Number) odds.get("probability")).doubleValue(), 5));
                }

                records.add(rec);
            }
        }

        return records;
    }

    public static List<MarketDataRecord> generateExpectedRecordsFromReport(Map<String, Object> report) {
        String eventId = String.valueOf(report.get("id"));
        List<Map<String, Object>> markets = (List<Map<String, Object>>) report.get("markets");

        List<MarketDataRecord> records = new ArrayList<>();

        for (Map<String, Object> market : markets) {
            Long marketTypeId = ((Number) market.get("market_type_id")).longValue();
            List<Map<String, Object>> selections = (List<Map<String, Object>>) market.get("selections");

            for (Map<String, Object> sel : selections) {
                Long selectionTypeId = ((Number) sel.get("selection_type_id")).longValue();
                String status = String.valueOf(sel.get("status"));

                double price = selectionTypeId % 2 == 0
                        ? 1.5 + selectionTypeId
                        : 2.5 + selectionTypeId;

                double probability = selectionTypeId % 2 == 0
                        ? 0.445 + (selectionTypeId / 10.0)
                        : 0.555 + (selectionTypeId / 10.0);

                MarketDataRecord rec = new MarketDataRecord();
                rec.setEventId(eventId);
                rec.setMarketTypeId(marketTypeId);
                rec.setSelectionTypeId(selectionTypeId);
                rec.setStatus(status);
                rec.setPrice(round(price, 2));
                rec.setProbability(round(probability, 5));

                records.add(rec);
            }
        }

        return records;
    }

    public static Map<String, Object> generateExpectedKafkaEventMessage(Map<String, Object> event) {
        List<Map<String, Object>> markets = (List<Map<String, Object>>) event.get("markets");

        Set<Long> marketIds = markets.stream()
                .map(m -> ((Number) m.get("market_type_id")).longValue())
                .collect(Collectors.toSet());

        Set<Long> selectionIds = markets.stream()
                .flatMap(m -> ((List<Map<String, Object>>) m.get("selections")).stream())
                .map(sel -> ((Number) sel.get("selection_type_id")).longValue())
                .collect(Collectors.toSet());

        return Map.of(
                "id", Long.valueOf(event.get("id").toString()),
                "is_success", true,
                "unique_markets_ids", new ArrayList<>(marketIds),
                "unique_selection_ids", new ArrayList<>(selectionIds)
        );
    }

    public static Map<String, Object> generateExpectedKafkaReportMessage(Map<String, Object> report) {
        String eventId = String.valueOf(report.get("id"));
        List<Map<String, Object>> markets = (List<Map<String, Object>>) report.get("markets");

        Set<Long> marketIds = markets.stream()
                .map(m -> ((Number) m.get("market_type_id")).longValue())
                .collect(Collectors.toSet());

        Set<Long> selectionIds = markets.stream()
                .flatMap(m -> ((List<Map<String, Object>>) m.get("selections")).stream())
                .map(sel -> ((Number) sel.get("selection_type_id")).longValue())
                .collect(Collectors.toSet());

        return Map.of(
                "id", Long.valueOf(report.get("id").toString()),
                "is_success", true,
                "processed_markets_ids", new ArrayList<>(marketIds),
                "processed_selections_ids", new ArrayList<>(selectionIds)
        );
    }

    private static String statusFromCode(int code) {
        String[] statuses = {
                "active", "suspended", "disabled", "win", "loss",
                "return", "half_win", "half_loss", "cancelled"
        };
        if (code < 0 || code >= statuses.length) {
            throw new IllegalArgumentException("Invalid status code: " + code);
        }
        return statuses[code];
    }

    private static double round(double value, int scale) {
        return BigDecimal.valueOf(value)
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
