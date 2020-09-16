package cn.springcamp.springdynamicdatasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired
    private DatabaseConfig databaseConfig;

    @Override
    public void run(String... args) {
        databaseConfig.getSchemas().stream().filter(r -> !r.getQuery().isEmpty()).forEach(current -> {
            String result = current.getJdbcTemplate().queryForObject(
                    current.getQuery(), String.class);
            System.out.println(current.getCode() + " content: " + result);
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
