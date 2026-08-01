package cn.springcamp.spring.httpinterface;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockRestServiceServer
class DemoApplicationTest {

    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void before() {
        this.mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("https://httpbin.org")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess("{\"get\": 200}", MediaType.APPLICATION_JSON));
        this.mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("https://httpbin.org")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess("{\"post\": 200}", MediaType.APPLICATION_JSON));
        this.mockRestServiceServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.requestTo(Matchers.startsWithIgnoringCase("https://httpbin.org")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(MockRestResponseCreators.withSuccess("{\"delete\": 200}", MediaType.APPLICATION_JSON));

        restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void testRemoteCallRest() {
        log.info("testRemoteCallRest get {}", restClient.get().uri("/foo").retrieve().body(String.class));
        log.info("testRemoteCallRest getById {}", restClient.get().uri("/foo/1").retrieve().body(String.class));
        log.info("testRemoteCallRest post {}", restClient.post().uri("/foo").body(new MyData(1L, "demo")).retrieve().body(String.class));
        restClient.delete().uri("/foo").retrieve().toBodilessEntity();
    }
}
