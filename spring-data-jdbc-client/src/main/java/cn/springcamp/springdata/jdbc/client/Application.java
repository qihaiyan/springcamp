package cn.springcamp.springdata.jdbc.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private DbService dbService;

    @Override
    public void run(String... args) {
        MyData myData = new MyData(1L, "test");
        log.info("insert rows: {}", dbService.insertDataWithObject(myData));

        MyData myData2 = new MyData(2L, "test");
        dbService.insertDataWithParam(myData2);

        MyData myData3 = new MyData(3L, "author");
        dbService.insertDataWithNamedParam(myData3);

        log.info("findDataById: {}", dbService.findDataById(1L));
        log.info("findDataByName: {}", dbService.findDataByName("test"));
        log.info("findDataWithRowMapper: {}", dbService.findDataWithRowMapper());
        log.info("findDataByParamMap: {}", dbService.findDataByParamMap(Map.of("name", "author")));
        log.info("countByName: {}", dbService.countByName("test"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
