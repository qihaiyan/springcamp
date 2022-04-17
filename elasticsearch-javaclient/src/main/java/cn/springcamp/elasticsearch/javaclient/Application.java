package cn.springcamp.elasticsearch.javaclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {
    @Autowired
    private DemoService demoService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        demoService.save();
        List<DemoDomain> demoDomains = demoService.findAll();
        DemoDomain demoDomain = demoService.findOne();
        demoService.deleteById("1");
        demoService.findAndDelete();
    }
}