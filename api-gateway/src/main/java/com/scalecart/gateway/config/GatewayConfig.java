package com.scalecart.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    /**
     * Rate limiting key = client IP address.
     *
     * Each unique IP gets its own rate limit bucket in Redis.
     * So 10 users from 10 different IPs each get their own
     * 10 req/sec allowance — they don't share one bucket.
     *
     * Alternative keys: user ID (from JWT), API key header,
     * combination of IP + user ID.
     *
     * Bean name "ipKeyResolver" matches the SpEL reference
     * in application.yml: key-resolver: "#{@ipKeyResolver}"
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }
}