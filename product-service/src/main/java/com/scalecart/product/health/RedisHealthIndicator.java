package com.scalecart.product.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component("redisCustomHealth")  // named to avoid conflict with Spring's built-in
public class RedisHealthIndicator implements HealthIndicator {

    private static final Logger log =
            LoggerFactory.getLogger(RedisHealthIndicator.class);

    private final RedisConnectionFactory connectionFactory;

    public RedisHealthIndicator(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Health health() {
        try (RedisConnection connection = connectionFactory.getConnection()) {
            // Ping Redis — verifies connection is alive
            String ping = connection.ping();

            if (!"PONG".equals(ping)) {
                return Health.down()
                        .withDetail("reason", "Redis ping failed")
                        .withDetail("response", ping)
                        .build();
            }

            // Get Redis server info for additional details
            Properties info = connection.serverCommands().info("server");

            String redisVersion = info != null
                    ? info.getProperty("redis_version", "unknown")
                    : "unknown";

            // Check memory usage
            Properties memInfo = connection.serverCommands().info("memory");

            String usedMemory = memInfo != null
                    ? memInfo.getProperty("used_memory_human", "unknown")
                    : "unknown";

            return Health.up()
                    .withDetail("status", "Redis is healthy")
                    .withDetail("redis_version", redisVersion)
                    .withDetail("used_memory", usedMemory)
                    .withDetail("ping", "PONG")
                    .build();

        } catch (Exception e) {
            log.error("Redis health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("reason", "Cannot connect to Redis")
                    .build();
        }
    }
}
