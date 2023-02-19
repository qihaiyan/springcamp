package cn.springcamp.springgroovy;

import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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
    public void run(String... args) throws ScriptException, ResourceException, IllegalAccessException, InstantiationException, javax.script.ScriptException, NoSuchMethodException {
        MyDomain myDomain = new MyDomain();
        myDomain.setName("test");
        System.out.println(myServiceXml.fun(myDomain));
        myDomain.setName("test2");
        System.out.println(myServiceGroovy.fun(myDomain));
        myEngine.runScript(1, 2);

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("groovy");
        engine.put("first", "HELLO");
        engine.put("second", "world");
        String fact = "def fun(n) { first.toLowerCase() + ' ' + second.toUpperCase() + ' ' + n}";
        engine.eval(fact);
        Invocable inv = (Invocable) engine;
        Object[] params = {"5"};
        Object result = inv.invokeFunction("fun", params);
        System.out.println("ScriptEngineManager result: " + result);
    }
}
