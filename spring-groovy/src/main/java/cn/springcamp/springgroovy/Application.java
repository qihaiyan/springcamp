package cn.springcamp.springgroovy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:xml-bean-config.xml", "classpath:BeanBuilder.groovy"})
public class Application implements CommandLineRunner {

    @Autowired
    private MyService myService;
    @Autowired
    private MyService myService2;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        MyDomain myDomain = new MyDomain();
        myDomain.setName("test");
        System.out.println(myService.fun(myDomain));
        myDomain.setName("test2");
        System.out.println(myService2.fun(myDomain));
    }
}
