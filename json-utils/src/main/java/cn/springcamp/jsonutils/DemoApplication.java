package cn.springcamp.jsonutils;

import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Override
    public void run(String... args) {
        JsonUtils.writeValue(new Demo());
        Demo demo = JsonUtils.readValue("{\"foo\":\"a\"}", Demo.class);
    }

    @Data
    public static class Demo {
        private String foo;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
