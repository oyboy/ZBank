package com.example.springtests.components;

import com.example.springtests.models.MarketDataRecord;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.springframework.stereotype.Component;

@Component
public class JdbiFactory {
    private static final Jdbi jdbi;
    static {
        jdbi = Jdbi.create(
                ConfigReader.getProperty("JDBC_URL"),
                ConfigReader.getProperty("JDBC_USER"),
                ConfigReader.getProperty("JDBC_PASS")
        );
        jdbi.registerRowMapper(BeanMapper.factory(MarketDataRecord.class));
    }
    public static Jdbi create() {
        return jdbi;
    }
}
