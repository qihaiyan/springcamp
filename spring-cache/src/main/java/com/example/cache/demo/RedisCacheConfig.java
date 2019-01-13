package com.example.cache.demo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCachePrefix;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Value("${spring.cache.redis.time-to-live:3600}")
    private Long keyExpiration;

    @Bean
    public RedisCacheManager redisCacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {

        ObjectMapper cborMapper = new ObjectMapper(new CBORFactory());
        ObjectMapper smileMapper = new ObjectMapper(new SmileFactory());
        smileMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        cborMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setDefaultExpiration(keyExpiration);
        cacheManager.setUsePrefix(true);
        cacheManager.setCachePrefix(new RedisCachePrefix() {
            private final RedisSerializer<String> serializer = new StringRedisSerializer();
            private final String delimiter = ":";

            public byte[] prefix(String cacheName) {
                return this.serializer
                        .serialize(cacheName.concat(this.delimiter));
            }
        });

        return cacheManager;
    }
}
