package cn.springcamp.springcache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@CacheConfig(cacheNames="greeting")
public class GreetingService {
    private static final Logger log = LoggerFactory.getLogger(GreetingService.class);

    @Cacheable(key = "#p0")
    public Greeting greeting(Long id) {
        try {
            Greeting greeting = new Greeting(id, "Hello, 你好!");
            return greeting;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
