package cn.springcamp.springboot.datetimetoepoch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

@SpringBootApplication
public class Application {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<String, String> handle() {
        return String::toUpperCase;
    }
}
