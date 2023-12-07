package cn.springcamp.spring.rest.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@SpringBootApplication
@RestController
public class Application {

    @Autowired
    private RestClient restClient;

    @GetMapping("/demo/list")
    public Object requestList() {
        List<String> list = restClient.get()
                .uri("http://someservice/list")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return list;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
