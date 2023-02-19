package cn.springcamp.springcloudgateway;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private RouteFilterRepository routeFilterRepository;

    @Test
    public void testRoute() {
        ResponseEntity<String> resp = testRestTemplate.exchange(RequestEntity.get("/route1/test").header("code", "alpha").build(), String.class);
        log.info("resp for route1: {}", resp);
    }
}
