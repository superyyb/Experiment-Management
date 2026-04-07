package com.rndlab.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Duration;

/**
 * Redis: Spring Cache for {@code experimentRecord} (TTL + {@code @CacheEvict} on writes). Search uses manual JSON cache in
 * {@link com.rndlab.backend.service.ExperimentRecordService} with hashed keys ({@link com.rndlab.backend.cache.ExperimentSearchCacheKeyGenerator}).
 */
@Configuration
@EnableCaching
public class RedisConfig {

    private static GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
    
    /**
     * 配置RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用String序列化器作为key的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // 使用JSON序列化器作为value的序列化器
        GenericJackson2JsonRedisSerializer json = jsonRedisSerializer();
        template.setValueSerializer(json);
        template.setHashValueSerializer(json);
        
        template.afterPropertiesSet();
        return template;
    }
    
    /**
     * 配置CacheManager
     * 设置不同缓存的TTL（生存时间）
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer json = jsonRedisSerializer();
        // 实验记录缓存配置：TTL 1小时
        RedisCacheConfiguration experimentRecordConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(json));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(experimentRecordConfig)
            .withCacheConfiguration("experimentRecord", experimentRecordConfig)
            .build();
    }
}

