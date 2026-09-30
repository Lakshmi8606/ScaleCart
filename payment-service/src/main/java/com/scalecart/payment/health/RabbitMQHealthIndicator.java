package com.scalecart.payment.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("rabbitMQCustomHealth")
public class RabbitMQHealthIndicator implements HealthIndicator {

    private static final Logger log =
            LoggerFactory.getLogger(RabbitMQHealthIndicator.class);

    private final ConnectionFactory connectionFactory;

    public RabbitMQHealthIndicator(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Health health() {
        try (Connection springConnection = connectionFactory.createConnection()) {
            com.rabbitmq.client.Connection connection = springConnection.getDelegate();

            if (connection == null || !connection.isOpen()) {
                return Health.down()
                        .withDetail("reason", "Connection is not open")
                        .build();
            }

            Object version = connection.getServerProperties().get("version");
            String serverVersion = version != null ? version.toString() : "unknown";

            return Health.up()
                    .withDetail("status", "RabbitMQ is healthy")
                    .withDetail("server_version", serverVersion)
                    .withDetail("virtual_host",
                            connectionFactory.getVirtualHost())
                    .withDetail("queue.order-status-update",
                            "CONFIGURED")
                    .build();

        } catch (Exception e) {
            log.error("RabbitMQ health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("reason", "Cannot connect to RabbitMQ")
                    .build();
        }
    }
}
