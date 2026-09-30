package com.codeconnect.curriculum.infrastructure.config;

import com.codeconnect.curriculum.infrastructure.config.properties.CacheTtlProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Distributed Redis Cache configuration for curriculum-service.
 * Configures per-domain TTLs, Jackson 2 JSON value serialization for Java 21 records,
 * and key namespacing.
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    private final CacheTtlProperties props;

    public RedisCacheConfig(CacheTtlProperties props) {
        this.props = props;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMillis(props.defaultTtlMs() > 0 ? props.defaultTtlMs() : 3600000))
            .disableCachingNullValues()
            .prefixCacheNameWith("codeconnect:cache:")
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("tracks", defaultCacheConfig.entryTtl(Duration.ofMillis(props.tracksTtlMs() > 0 ? props.tracksTtlMs() : 7200000)));
        cacheConfigurations.put("modules", defaultCacheConfig.entryTtl(Duration.ofMillis(props.modulesTtlMs() > 0 ? props.modulesTtlMs() : 3600000)));
        cacheConfigurations.put("lessons", defaultCacheConfig.entryTtl(Duration.ofMillis(props.lessonsTtlMs() > 0 ? props.lessonsTtlMs() : 3600000)));
        cacheConfigurations.put("student_progress", defaultCacheConfig.entryTtl(Duration.ofMillis(props.progressTtlMs() > 0 ? props.progressTtlMs() : 300000)));

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultCacheConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
