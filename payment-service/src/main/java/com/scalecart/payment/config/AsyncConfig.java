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

        // Minimum threads always kept alive
        executor.setCorePoolSize(5);

        // Maximum threads allowed under spike load
        executor.setMaxPoolSize(20);

        // Queue size - requests wait here if all 20 threads are busy
        executor.setQueueCapacity(100);

        // Thread name prefix - visible in logs, helps debugging
        executor.setThreadNamePrefix("webhook-async-");

        executor.initialize();
        return executor;
    }
}