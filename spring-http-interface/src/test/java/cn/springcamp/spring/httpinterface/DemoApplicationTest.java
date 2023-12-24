package cn.springcamp.spring.httpinterface;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MyService myService;

    private MockRestServiceServer mockRestServiceServer;
    private MockServerRestClientCustomizer mockServerRestClientCustomizer;

    @Before
    public void before() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
        this.mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("https://httpbin.org")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess("{\"get\": 200}", MediaType.APPLICATION_JSON));
        this.mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("https://httpbin.org")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess("{\"post\": 200}", MediaType.APPLICATION_JSON));
        this.mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("https://httpbin.org")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(MockRestResponseCreators.withSuccess("{\"delete\": 200}", MediaType.APPLICATION_JSON));
    }

    @Test
    public void testRemoteCallRest() {
        log.info("testRemoteCallRest get {}", testRestTemplate.getForObject("/foo", String.class));
        log.info("testRemoteCallRest getById {}", testRestTemplate.getForObject("/foo/1", String.class));
        log.info("testRemoteCallRest post {}", testRestTemplate.postForObject("/foo", new MyData(1L, "demo"), String.class));
        testRestTemplate.exchange("/foo", HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
    }
}
