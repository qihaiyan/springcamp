package cn.springcamp.springboot.unit.test.service;

import cn.springcamp.springboot.unit.test.data.MyDomain;
import cn.springcamp.springboot.unit.test.data.MyDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MyService {
    @Value("${common.value}")
    private String originValue;

    @Autowired
    private RestClient restClient;
    @Autowired
    private MyDomainRepository myDomainRepository;

    @Cacheable(cacheNames = "redis")
    public String cacheFunc() {
        return "ok";
    }

    public MyDomain dbFunc(Long id) {
        return myDomainRepository.findById(id).orElse(new MyDomain());
    }

    public Page<MyDomain> dbPageFunc(Pageable pageable) {
        return myDomainRepository.findAll(pageable);
    }

    public Slice<MyDomain> dbSliceFunc(Pageable pageable) {
        return myDomainRepository.findAll(pageable);
    }

    public String callRemote() {
        return restClient.get().uri("http://someservice/foo").retrieve().body(String.class);
    }

    public String getOriginValue() {
        return this.originValue;
    }
}
