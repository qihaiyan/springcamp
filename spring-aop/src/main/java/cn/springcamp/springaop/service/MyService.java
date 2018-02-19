package cn.springcamp.springaop.service;

import cn.springcamp.springaop.aop.TrackTime;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MyService {

    @TrackTime(param = "myService")
    public String runFoo() {
        log.info(" -------------> foo");
        return "foo";
    }
}
