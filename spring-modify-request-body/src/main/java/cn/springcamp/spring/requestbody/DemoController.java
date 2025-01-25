package cn.springcamp.spring.requestbody;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController {
    @PostMapping("/test")
    public ReqBody test(@RequestBody ReqBody reqBody) {
        return reqBody;
    }
}