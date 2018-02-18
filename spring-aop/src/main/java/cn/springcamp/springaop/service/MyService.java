package cn.springcamp.springaop.service;

import cn.springcamp.springaop.aop.TrackTime;
import org.springframework.stereotype.Component;

@Component
public class MyService {

    @TrackTime(param = "myService")
    public String runFoo() {
        System.out.println(" -------------> foo");
        return "foo";
    }
}
