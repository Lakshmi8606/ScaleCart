package com.scalecart.auth.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("databaseCustomHealth")
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final Logger log =
            LoggerFactory.getLogger(DatabaseHealthIndicator.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            // SELECT 1 — lightest possible query that proves DB is up
            // and connection pool is working
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            // Check critical data exists — roles seeded by Flyway
            Integer roleCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM roles", Integer.class);

            Integer userCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users", Integer.class);

            if (roleCount == null || roleCount == 0) {
                return Health.down()
                        .withDetail("reason",
                                "No roles found — Flyway migration may have failed")
                        .build();
            }

            return Health.up()
                    .withDetail("status", "PostgreSQL is healthy")
                    .withDetail("database", "scalecart_auth_db")
                    .withDetail("roles_count", roleCount)
                    .withDetail("users_count", userCount)
                    .withDetail("connection", "OK")
                    .build();

        } catch (Exception e) {
            log.error("Database health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("reason", "Cannot query PostgreSQL")
                    .build();
        }
    }
}