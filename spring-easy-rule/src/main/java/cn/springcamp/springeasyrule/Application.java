package cn.springcamp.springeasyrule;

import org.jeasy.rules.api.Facts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    RuleEngineTemplate ruleEngineTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        Person person = new Person();
        Facts facts = new Facts();
        facts.put("person", person);
        ruleEngineTemplate.fire("demo", facts);

    }
}
