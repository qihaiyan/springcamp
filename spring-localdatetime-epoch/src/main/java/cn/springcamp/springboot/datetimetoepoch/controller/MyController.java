package cn.springcamp.springboot.datetimetoepoch.controller;

import cn.springcamp.springboot.datetimetoepoch.data.MyDomain;
import cn.springcamp.springboot.datetimetoepoch.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyController {
    @Autowired
    private MyService service;

    @GetMapping("/db")
    public List<MyDomain> dbCallController(Long id) {
        return service.dbFunc();
    }
}
