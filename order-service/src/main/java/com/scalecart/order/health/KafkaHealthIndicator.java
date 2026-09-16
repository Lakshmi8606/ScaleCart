package com.scalecart.order.health;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component("kafkaCustomHealth")
public class KafkaHealthIndicator implements HealthIndicator {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaHealthIndicator.class);

    private final KafkaAdmin kafkaAdmin;

    public KafkaHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public Health health() {
        Map<String, Object> props =
                new HashMap<>(kafkaAdmin.getConfigurationProperties());
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5_000);
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5_000);
        props.put(AdminClientConfig.SOCKET_CONNECTION_SETUP_TIMEOUT_MS_CONFIG, 5_000);
        props.put(AdminClientConfig.RETRIES_CONFIG, 1);

        try (AdminClient adminClient = AdminClient.create(props)) {

            DescribeClusterResult clusterResult =
                    adminClient.describeCluster();

            String clusterId = clusterResult.clusterId()
                    .get(5, TimeUnit.SECONDS);

            int brokerCount = clusterResult.nodes()
                    .get(5, TimeUnit.SECONDS)
                    .size();

            var topics = adminClient.listTopics()
                    .names()
                    .get(5, TimeUnit.SECONDS);

            boolean orderCreatedExists = topics.contains("order.created");
            boolean orderPaidExists    = topics.contains("order.paid");

            return Health.up()
                    .withDetail("status", "Kafka is healthy")
                    .withDetail("clusterId", clusterId)
                    .withDetail("brokerCount", brokerCount)
                    .withDetail("topic.order.created",
                            orderCreatedExists ? "EXISTS" : "MISSING")
                    .withDetail("topic.order.paid",
                            orderPaidExists ? "EXISTS" : "MISSING")
                    .build();

        } catch (Exception e) {
            log.error("Kafka health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("reason", "Cannot connect to Kafka broker")
                    .build();
        }
    }
}
