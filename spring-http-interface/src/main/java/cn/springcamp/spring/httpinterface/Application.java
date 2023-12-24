package cn.springcamp.spring.httpinterface;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private MyService myService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("get data: {}", myService.getData("myHeader"));
        log.info("get data by id: {}", myService.getData(1L));
        log.info("save data: {}", myService.saveData(new MyData(1L, "test")));
        log.info("delete data by id: {}", myService.deleteData(1L));
    }
}
