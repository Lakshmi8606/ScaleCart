package com.scalecart.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.payment}")
    private String paymentExchange;

    @Value("${rabbitmq.routing-key.order-paid}")
    private String orderPaidRoutingKey;

    @Value("${rabbitmq.queue.order-status-update}")
    private String orderStatusUpdateQueue;

    // ── Declare the Queue ──────────────────────────────────────────────
    // durable=true: queue survives RabbitMQ restart (messages not lost)
    @Bean
    public Queue orderStatusUpdateQueue() {
        return new Queue(orderStatusUpdateQueue, true);
    }

    // ── Declare the Exchange ───────────────────────────────────────────
    // DirectExchange: routes messages by exact routing key match
    // durable=true: exchange survives RabbitMQ restart
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(paymentExchange, true, false);
    }

    // ── Declare the Binding ────────────────────────────────────────────
    // Connects exchange → queue via routing key
    // When producer sends to paymentExchange with key "payment.paid"
    // → message lands in orderStatusUpdateQueue
    @Bean
    public Binding orderStatusBinding() {
        return BindingBuilder
                .bind(orderStatusUpdateQueue())
                .to(paymentExchange())
                .with(orderPaidRoutingKey);
    }

    // ── Message Converter ──────────────────────────────────────────────
    // Converts Java objects to JSON for RabbitMQ messages
    // Without this, Spring uses Java serialization (binary, not human-readable)
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    // ── RabbitTemplate ─────────────────────────────────────────────────
    // The main class for publishing messages — equivalent of KafkaTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}