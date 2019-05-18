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
    public List<Greeting> greeting(Long id) {
        try {
            List<Greeting>  greetings = new ArrayList<>();
            Greeting greeting = new Greeting(id, "Hello, 你好 Community!");
            greetings.add(greeting);
            Thread.sleep(500);
            return greetings;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
