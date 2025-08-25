package ru.otus.hw.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class H2TableHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbc;

    @Override
    public Health health() {
        long books = jdbc.queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'BOOKS'",
                Long.class
        );
        long authors = jdbc.queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'AUTHORS'",
                Long.class
        );
        long genres = jdbc.queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'GENRES'",
                Long.class
        );
        long comments = jdbc.queryForObject(
                "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'COMMENTS'",
                Long.class
        );

        if (books == 1 && authors == 1 && genres == 1 && comments == 1) {
            return Health.up().build();
        }
        return Health.down().build();
    }
}
