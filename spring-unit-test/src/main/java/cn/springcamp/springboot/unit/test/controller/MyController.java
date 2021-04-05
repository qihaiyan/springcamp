package cn.springcamp.springboot.unit.test.controller;

import cn.springcamp.springboot.unit.test.data.MyDomain;
import cn.springcamp.springboot.unit.test.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
    @Autowired
    private MyService service;

    @GetMapping("/remote")
    public String remoteCallController() {
        return service.callRemote();
    }

    @GetMapping("/cache")
    public String cacheCallController() {
        return service.cacheFunc();
    }

    @GetMapping("/db")
    public MyDomain dbCallController(Long id) {
        return service.dbFunc(id);
    }
}
