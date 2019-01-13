package com.example.cache.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
public class GreetingController {
    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

//    @Autowired
//    protected HttpServletRequest httpServletRequest;

    @Autowired
    private GreetingService greetingService;

    @PostMapping("/greeting")
    public Greeting greeting(HttpServletRequest httpServletRequest, String p1) {
        Map<String, String[]> params = httpServletRequest.getParameterMap();
        Greeting greeting = greetingService.greeting();
        if (params.size() > 0) {
            return greeting;//new Greeting(1L, "Hello, 你好 Community!");//"{\"content\":\"Hello, 你好 Community!\"}";
        }
        String test = "";
        try {
            if ("POST".equalsIgnoreCase(httpServletRequest.getMethod())) {
                test = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
                log.info(test);
            }
        } catch (Exception e) {
            test = "error";
            log.info(e.getMessage(), e);
        }
        return new Greeting();
    }
}
