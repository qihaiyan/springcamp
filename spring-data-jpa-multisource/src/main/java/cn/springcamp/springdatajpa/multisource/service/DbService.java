package cn.springcamp.springdatajpa.multisource.service;

import cn.springcamp.springdatajpa.multisource.other.data.Other;
import cn.springcamp.springdatajpa.multisource.other.data.OtherRepository;
import cn.springcamp.springdatajpa.multisource.primary.data.PrimaryTable;
import cn.springcamp.springdatajpa.multisource.primary.data.PrimaryTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DbService {

    @Autowired
    private PrimaryTableRepository primaryTableRepository;

    @Autowired
    private OtherRepository otherRepository;

    @Value("${name:World}")
    private String name;

    public String getHelloMessage() {
        PrimaryTable primaryTable = new PrimaryTable();
        primaryTable.setName("primary");
        primaryTableRepository.save(primaryTable);

        Other other = new Other();
        other.setName("other");
        otherRepository.save(other);

        primaryTable = primaryTableRepository.findById(1L).orElse(new PrimaryTable());
        other = otherRepository.findById(1L).orElse(new Other());
        return "Hello " + this.name + " : primary's value = " + primaryTable.getName() + " , other's value = " + other.getName();

    }

}

