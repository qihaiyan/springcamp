package cn.springcamp.springboot.unit.test.service;

import cn.springcamp.springboot.unit.test.data.MyDomain;
import cn.springcamp.springboot.unit.test.data.MyDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MyService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MyDomainRepository myDomainRepository;

    @Cacheable
    public String cacheFunc() {
        return "ok";
    }

    public MyDomain dbFunc(Long id) {
        return myDomainRepository.findById(id).orElse(new MyDomain());
    }

    public String callRemote() {
        return restTemplate.getForObject("http://someservice/foo", String.class);
    }
}
