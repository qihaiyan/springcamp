package cn.springcamp.spring.rest.client;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestClient restClient;
    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void before() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();

        this.mockRestServiceServer.expect(manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("http://someservice/list")))
                .andRespond(withSuccess("[{\"code\": \"200\"}]", MediaType.APPLICATION_JSON));

    }

    @Test
    public void testGet() {
        String resp = restClient.get()
                .uri("https://httpbin.org/get")
                .retrieve()
                .body(String.class);
        log.info("get: {}", resp);

        MyData myData = restClient.get()
                .uri("https://httpbin.org/get")
                .retrieve()
                .body(MyData.class);
        log.info("get: {}", myData);

        MyData postBody = new MyData("test", "test RestClient");
        ResponseEntity<String> respObj = restClient.post()
                .uri("https://httpbin.org/post")
                .contentType(MediaType.APPLICATION_JSON)
                .body(postBody)
                .retrieve()
                .toEntity(String.class);
        log.info("post response: {}", respObj);

        // Error handling
        restClient.get()
                .uri("https://httpbin.org/status/404")
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    log.info("status 404");
                })
                .toBodilessEntity();

        // Exchange
        restClient.get()
                .uri("https://httpbin.org/get")
                .accept(MediaType.APPLICATION_JSON)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is4xxClientError()) {
                        log.info("status 4xx");
                        return "";
                    } else {
                        log.info("response: {}", response);
                        return response;
                    }
                });

//        testRestTemplate.getForObject("/demo/list", String.class);
    }
}
