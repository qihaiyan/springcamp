package cn.springcamp.springboot.unit.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

@EnableCaching
@SpringBootApplication
@ComponentScan("cn.springcamp.springboot.unit.test")
public class Application {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<String, String> handle() {
        Assert.hasText("11");
        return String::toUpperCase;
    }
}
