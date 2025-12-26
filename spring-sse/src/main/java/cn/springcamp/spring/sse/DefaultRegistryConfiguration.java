package cn.springcamp.spring.sse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultRegistryConfiguration {

    @Bean(name = "registry", destroyMethod = "shutdown")
    @ConditionalOnMissingBean(SseRegistry.class)
    public SseRegistry<Object, Object> defaultSseRegistry() {
        return SseRegistry.builder()
                .maxStreams(100)
                .maxEvents(100)
                .timeout(60_000L)
                .build();
    }
}
