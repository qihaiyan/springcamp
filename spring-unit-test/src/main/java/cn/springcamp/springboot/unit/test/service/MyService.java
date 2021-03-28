package cn.springcamp.springboot.unit.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MyService {
    @Autowired
    private RestTemplate restTemplate;

    @Cacheable
    public String cacheFunc() {
        return "ok";
    }

    public String callRemote() {
        return restTemplate.getForObject("http://someservice/foo", String.class);
    }
}
