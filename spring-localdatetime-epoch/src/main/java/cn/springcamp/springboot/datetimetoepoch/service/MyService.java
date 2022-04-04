package cn.springcamp.springboot.datetimetoepoch.service;

import cn.springcamp.springboot.datetimetoepoch.data.MyDomain;
import cn.springcamp.springboot.datetimetoepoch.data.MyDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

    public List<MyDomain> dbFunc() {
        return myDomainRepository.findAll();
    }
}
