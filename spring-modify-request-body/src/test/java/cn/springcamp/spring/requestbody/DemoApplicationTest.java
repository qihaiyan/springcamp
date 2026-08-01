package cn.springcamp.spring.requestbody;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void before() {
        restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void test() {
        ReqBody reqBody = new ReqBody();
        ResponseEntity<ReqBody> resp = restClient.post().uri("/test").header("foo", "test").body(reqBody).retrieve().toEntity(ReqBody.class);
        log.info("result : {}", resp);
        assertThat(resp.getBody().getFoo(), is("test"));
    }
}
