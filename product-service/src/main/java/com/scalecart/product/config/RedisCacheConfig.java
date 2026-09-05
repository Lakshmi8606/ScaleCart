package com.scalecart.product.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.scalecart.product.entity.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            @Value("${spring.cache.redis.time-to-live:600000}") long ttlMs) {

        ObjectMapper mapper = redisObjectMapper();

        // Typed serializer for single Product cache entries
        Jackson2JsonRedisSerializer<Product> productSerializer =
                new Jackson2JsonRedisSerializer<>(mapper, Product.class);

        RedisCacheConfiguration productCache = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(ttlMs))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(productSerializer));

        // List/page caches must not store Hibernate PageImpl or lazy proxies —
        // those fail to deserialize and turn GET /api/products into HTTP 500.
        ObjectMapper polymorphicMapper = mapper.copy();
        polymorphicMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        GenericJackson2JsonRedisSerializer defaultSerializer =
                new GenericJackson2JsonRedisSerializer(polymorphicMapper);

        RedisCacheConfiguration defaultCache = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(ttlMs))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(defaultSerializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCache)
                .withCacheConfiguration("product", productCache)
                .build();
    }
}
