package cn.springcamp.springnative;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {
    @LocalServerPort
    private int port;
    private RestClient restClient;

    @BeforeEach
    void before() {
        restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void testHello() {
        String resp = restClient.get().uri("/hello").retrieve().body(String.class);
        log.info("hello result : {}", resp);
        assertThat(resp, is("{\"id\":1,\"name\":\"test\"}"));
    }
}
