package com.example.springtests.extensions;

import com.example.springtests.repositories.MarketRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static org.junit.jupiter.api.Assertions.fail;

public class DatabaseCleanerExtension implements BeforeEachCallback {
    private final MarketRepository marketRepository = new MarketRepository();

    @Override
    public void beforeEach(ExtensionContext context) {
        try {
            marketRepository.clearMarketData();
        } catch (Exception e) {
            fail("Ошибка при очистке БД", e);
        }
    }
}
