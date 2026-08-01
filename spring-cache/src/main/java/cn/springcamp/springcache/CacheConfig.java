package cn.springcamp.springcache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.autoconfigure.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.jspecify.annotations.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tools.jackson.databind.cfg.DateTimeFeature;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    @Value("${spring.cache.redis.time-to-live:3600}")
    private Long keyExpiration;

    @Bean(name = "cacheProperties")
    @ConfigurationProperties("spring.cache")
    public CacheProperties cacheProperties() {
        return new CacheProperties();
    }

    /**
     * Builds a Jackson 3-based {@link GenericJacksonJsonRedisSerializer} with default (polymorphic)
     * typing enabled so that type information is embedded as a {@code @class} property.
     * <p>
     * This mirrors the prior {@code GenericJackson2JsonRedisSerializer} setup that used a permissive
     * subtype validator. {@code enableUnsafeDefaultTyping()} applies the same permissive validation
     * (allow all subtypes) — only safe because Redis cache contents are written by this application itself.
     */
    private GenericJacksonJsonRedisSerializer jsonRedisSerializer() {
        return GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .customize(builder -> builder
                        // JavaTime/Jdk8 support is built into Jackson 3 core, no module registration needed.
                        .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS))
                .build();
    }

    @Bean
    @Primary
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(keyExpiration))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer()))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> cacheName.concat(":"));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();

    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
                log.error("cache error, cache={} key={}", cache.getName(), key, e);
            }

            @Override
            public void handleCachePutError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key, Object value) {
                log.error("cache error, cache={} key={}", cache.getName(), key, e);
            }

            @Override
            public void handleCacheEvictError(@NonNull RuntimeException e, @NonNull Cache cache, @NonNull Object key) {
                log.error("cache error, cache={} key={}", cache.getName(), key, e);
            }

            @Override
            public void handleCacheClearError(@NonNull RuntimeException e, @NonNull Cache cache) {
                log.error("cache error", e);
            }
        };
    }

    @Bean
    @Qualifier("localCacheManager")
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = createCacheManager();
        if (!CollectionUtils.isEmpty(cacheProperties().getCacheNames())) {
            cacheManager.setCacheNames(cacheProperties().getCacheNames());
        }
        return cacheManager;
    }

    private CaffeineCacheManager createCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        setCacheBuilder(cacheManager);
        return cacheManager;
    }

    private void setCacheBuilder(CaffeineCacheManager cacheManager) {
        String specification = cacheProperties().getCaffeine().getSpec();
        if (StringUtils.hasText(specification)) {
            cacheManager.setCacheSpecification(specification);
        }
        cacheManager.setAllowNullValues(false);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(jsonRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
