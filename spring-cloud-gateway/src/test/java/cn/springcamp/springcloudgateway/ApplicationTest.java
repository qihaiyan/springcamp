package cn.springcamp.springcloudgateway;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.reactive.server.WebTestClient;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApplicationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RouteFilterRepository routeFilterRepository;
    @LocalServerPort
    private int port;

    @Test
    void testRoute() {
        String body = webTestClient.method(HttpMethod.GET).uri("/route1/test?a=test")
                .header("code", "alpha")
                .exchange()
                .expectBody(String.class)
                .returnResult().getResponseBody();
        log.info("resp for route1: {}", body);
    }
}
