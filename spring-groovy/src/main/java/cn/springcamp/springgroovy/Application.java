package cn.springcamp.springgroovy;

import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:xml-bean-config.xml", "classpath:BeanBuilder.groovy"})
public class Application implements CommandLineRunner {

    @Autowired
    private MyService myServiceXml;
    @Autowired
    private MyService myServiceGroovy;
    @Autowired
    private MyEngine myEngine;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws ScriptException, ResourceException, IllegalAccessException, InstantiationException {
        MyDomain myDomain = new MyDomain();
        myDomain.setName("test");
        System.out.println(myServiceXml.fun(myDomain));
        myDomain.setName("test2");
        System.out.println(myServiceGroovy.fun(myDomain));
        myEngine.runScript(1, 2);
    }
}
