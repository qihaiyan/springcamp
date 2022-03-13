package cn.springcamp.springcache;

import com.github.fppt.jedismock.RedisServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class TestRedisConfiguration {

    private final RedisServer redisServer;

    public TestRedisConfiguration(@Value("${spring.redis.port}") final int redisPort) {
        redisServer = RedisServer.newRedisServer(redisPort);
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        try {
            redisServer.stop();
        } catch (IOException ignored) {

        }
    }
}
