package cn.springcamp.spring.rest.client;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@Slf4j
@RunWith(SpringRunner.class)
@RestClientTest()
@Import(RestClientConfig.class)
public class DemoRestClientTest {

    @Autowired
    private RestClient restClient;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Before
    public void before() {
        this.mockRestServiceServer.expect(manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("http://someservice/list")))
                .andRespond(withSuccess("{\"code\": \"200\"}", MediaType.APPLICATION_JSON));
        this.mockRestServiceServer.expect(manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("http://someservice/status/404")))
                .andRespond(withResourceNotFound());
    }

    @Test
    public void testGet() {
        String resp = restClient.get()
                .uri("http://someservice/list")
                .retrieve()
                .body(String.class);
        log.info("get: {}", resp);

        MyData myData = restClient.get()
                .uri("http://someservice/list")
                .retrieve()
                .body(MyData.class);
        log.info("get: {}", myData);

        MyData postBody = new MyData("test", "test RestClient");
        ResponseEntity<String> respObj = restClient.post()
                .uri("http://someservice/list")
                .contentType(MediaType.APPLICATION_JSON)
                .body(postBody)
                .retrieve()
                .toEntity(String.class);
        log.info("post response: {}", respObj);

        // Error handling
        restClient.get()
                .uri("http://someservice/status/404")
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    log.info("status 404");
                })
                .toBodilessEntity();

        // Exchange
        restClient.get()
                .uri("http://someservice/list")
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
