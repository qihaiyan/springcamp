package cn.springcamp.springlogrequestresponse;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DemoController {
    @GetMapping("/demo/get")
    public Object demoGet(String arg) {
        return arg;
    }

    @PostMapping("/demo/post/form")
    public Object demoPostForm(String arg) {
        return arg;
    }

    @PostMapping("/demo/post/body")
    public Object demoPostBody(@RequestBody BodyRequest request) {
        return request;
    }

    @Data
    static class BodyRequest {
        private String arg1;
    }
}
