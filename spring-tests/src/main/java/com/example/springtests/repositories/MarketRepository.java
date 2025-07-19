package com.example.springtests.repositories;

import com.example.springtests.components.JdbiFactory;
import com.example.springtests.models.MarketDataRecord;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MarketRepository {
    private final Jdbi jdbi = JdbiFactory.create();

    public void clearMarketData() {
        jdbi.useHandle(handle ->
                handle.execute("TRUNCATE TABLE market_data RESTART IDENTITY")
        );
    }

    public List<MarketDataRecord> getMarketDataByEventId(String eventId) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT id, event_id, market_type_id, selection_type_id, price, probability, status " +
                                "FROM market_data WHERE event_id = :eventId::bigint ORDER BY selection_type_id")
                        .bind("eventId", eventId)
                        .mapTo(MarketDataRecord.class)
                        .list()
        );
    }

    public List<MarketDataRecord> getMarketDataByReportId(String reportId) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM market_data WHERE event_id = :reportId::bigint ORDER BY selection_type_id")
                        .bind("reportId", reportId)
                        .mapTo(MarketDataRecord.class)
                        .list());
    }
}
