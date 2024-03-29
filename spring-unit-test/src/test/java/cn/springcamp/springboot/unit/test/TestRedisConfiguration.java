package cn.springcamp.springboot.unit.test;

import com.github.fppt.jedismock.RedisServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import java.io.IOException;

@TestConfiguration
public class TestRedisConfiguration {

    private final RedisServer redisServer;

    public TestRedisConfiguration(@Value("${spring.data.redis.port}") final int redisPort) {
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
