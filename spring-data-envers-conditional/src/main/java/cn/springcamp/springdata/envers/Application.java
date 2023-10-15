package cn.springcamp.springdata.envers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private DbService dbService;

    @Override
    public void run(String... args) {
        MyData myData = new MyData();
        myData.setId(1L);
        myData.setAuthor("test");
        dbService.saveData(myData);
        dbService.findRevisions(myData.getId()).forEach(r -> System.out.println("revision: " + r.toString()));


        myData.setAuthor("newAuthor");
        dbService.saveData(myData);
        dbService.findRevisions(myData.getId()).forEach(r -> System.out.println("revision: " + r.toString()));

        // won't generate audit record when author is null
        myData.setAuthor(null);
        dbService.saveData(myData);
        dbService.findRevisions(myData.getId()).forEach(r -> System.out.println("revision: " + r.toString()));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
