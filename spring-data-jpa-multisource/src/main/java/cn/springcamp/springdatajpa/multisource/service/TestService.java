package cn.springcamp.springdatajpa.multisource.service;

import cn.springcamp.springdatajpa.multisource.other.data.Other;
import cn.springcamp.springdatajpa.multisource.other.data.OtherRepository;
import cn.springcamp.springdatajpa.multisource.test.data.Test;
import cn.springcamp.springdatajpa.multisource.test.data.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestService {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private OtherRepository otherRepository;

    @Value("${name:World}")
    private String name;

    public String getHelloMessage() {
        Test test = new Test();
        test.setId(1);
        test = testRepository.save(test);

        Other other = new Other();
        other.setId(2);
        other = otherRepository.save(other);

        test = testRepository.findOne(1);
        return "Hello " + this.name + " : test's value = " + test.getId() + " , other's value = " + other.getId();

    }

}

