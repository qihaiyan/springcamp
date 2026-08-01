package cn.springcamp.springresttemplatelog;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTest {

    @Autowired
    private RestTemplate restTemplate;
    @LocalServerPort
    private int port;
    private MockRestServiceServer mockRestServiceServer;
    private RestClient restClient;

    @BeforeEach
    void before() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();

        this.mockRestServiceServer.expect(manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("http://someservice/foo")))
                .andRespond(withSuccess("{\"code\": \"200\"}", MediaType.APPLICATION_JSON));

        restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void testGet() {
        String resp = restClient.get().uri("/demo/get?arg=test").retrieve().body(String.class);
        log.info("rest: {}", resp);
    }
}
