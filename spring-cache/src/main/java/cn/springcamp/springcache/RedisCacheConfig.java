package com.example.cache.demo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
//import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {

    @Value("${spring.cache.redis.time-to-live:3600}")
    private Long keyExpiration;

//    @Bean
//    private RedisCacheConfiguration cacheConfiguration() {
//        ObjectMapper cborMapper = new ObjectMapper(new CBORFactory());
////        ObjectMapper smileMapper = new ObjectMapper(new SmileFactory());
////        smileMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//        cborMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(keyExpiration))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(cborMapper)))
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .disableCachingNullValues();
//    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

//        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
//        cacheConfiguration = cacheConfiguration.serializeValuesWith(valueSerializationPair);
////        cacheConfiguration = cacheConfiguration.prefixKeysWith("myPrefix");
//        cacheConfiguration = cacheConfiguration.entryTtl(Duration.ofSeconds(30));

        ObjectMapper cborMapper = new ObjectMapper(new CBORFactory());
//        ObjectMapper smileMapper = new ObjectMapper(new SmileFactory());
//        smileMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        cborMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(keyExpiration))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(cborMapper)))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> cacheName.concat(":"));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();

//        return new RedisCacheManager(redisCacheWriter, cacheConfiguration);


//        redisTemplate.setKeySerializer(new GenericToStringSerializer<>(Object.class));
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(cborMapper));
//
//        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        cacheManager.setDefaultExpiration(keyExpiration);
//        cacheManager.setUsePrefix(true);
//        cacheManager.setCachePrefix(new RedisCachePrefix() {
//            private final RedisSerializer<String> serializer = new StringRedisSerializer();
//            private final String delimiter = ":";
//
//            public byte[] prefix(String cacheName) {
//                return this.serializer
//                        .serialize(cacheName.concat(this.delimiter));
//            }
//        });
//
//        return cacheManager;
    }
}
