package com.karandashov.jbcd.api.news.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConfiguration {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(
            @Value("${spring.cache.redis.time-to-live}") Duration timeToLive,
            @Value("${spring.cache.redis.cache-null-values:false}") boolean cacheNullValues
    ) {
        GenericJacksonJsonRedisSerializer jsonSerializer = GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .build();

        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(timeToLive)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        if (!cacheNullValues) {
            configuration = configuration.disableCachingNullValues();
        }

        return configuration;
    }
}
