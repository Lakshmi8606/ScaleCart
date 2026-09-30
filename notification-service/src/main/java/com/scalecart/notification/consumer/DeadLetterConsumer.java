package com.scalecart.notification.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(DeadLetterConsumer.class);

    // Spring names the DLT as <topic>.DLT
    @KafkaListener(
            topics = "order.created.DLT",
            groupId = "notification-service-dlq"
    )
    public void handleDeadLetter(ConsumerRecord<String, Object> deadLetter) {
        persistAndAlert(deadLetter);
    }

    private void persistAndAlert(ConsumerRecord<String, Object> deadLetter) {
        // Structured error log is the operator audit trail for this service.
        log.error("DEAD LETTER received: topic={}, partition={}, " +
                        "offset={}, key={}, value={}",
                deadLetter.topic(),
                deadLetter.partition(),
                deadLetter.offset(),
                deadLetter.key(),
                deadLetter.value());
    }
}
