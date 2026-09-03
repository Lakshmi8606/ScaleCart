package com.scalecart.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "webhookTaskExecutor")
    public Executor webhookTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);

        executor.setMaxPoolSize(20);

        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("webhook-async-");

        executor.initialize();
        return executor;
    }
}
