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

    @Bean
    public Queue orderStatusUpdateQueue() {
        return new Queue(orderStatusUpdateQueue, true);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(paymentExchange, true, false);
    }

    @Bean
    public Binding orderStatusBinding() {
        return BindingBuilder
                .bind(orderStatusUpdateQueue())
                .to(paymentExchange())
                .with(orderPaidRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
