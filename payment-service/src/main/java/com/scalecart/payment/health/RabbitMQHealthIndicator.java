package com.scalecart.payment.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        try {
            // Attempt to open a connection to RabbitMQ
            com.rabbitmq.client.Connection connection =
                    connectionFactory.createConnection()
                            .getDelegate();

            if (connection.isOpen()) {
                String serverVersion = connection
                        .getServerProperties()
                        .get("version")
                        .toString();

                return Health.up()
                        .withDetail("status", "RabbitMQ is healthy")
                        .withDetail("server_version", serverVersion)
                        .withDetail("virtual_host",
                                connectionFactory.getVirtualHost())
                        .withDetail("queue.order-status-update",
                                "CONFIGURED")
                        .build();
            } else {
                return Health.down()
                        .withDetail("reason", "Connection is not open")
                        .build();
            }

        } catch (Exception e) {
            log.error("RabbitMQ health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("reason", "Cannot connect to RabbitMQ")
                    .build();
        }
    }
}