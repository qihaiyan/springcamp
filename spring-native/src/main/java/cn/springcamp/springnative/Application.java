package cn.springcamp.springnative;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {
    @Autowired
    private DbService dbService;

    @RequestMapping("/hello")
    public DemoData hello() {
        return dbService.hello();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}