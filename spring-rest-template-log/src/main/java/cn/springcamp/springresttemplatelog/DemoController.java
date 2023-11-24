package cn.springcamp.springresttemplatelog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class DemoController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/demo/get")
    public BodyRequest demoGet(@RequestParam(value = "arg") String arg) {
        return restTemplate.postForObject("http://someservice/foo", new BodyRequest(arg), BodyRequest.class);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BodyRequest {
        private String code;
    }
}
