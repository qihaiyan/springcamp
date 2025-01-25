package cn.springcamp.spring.requestbody;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void test() {
        ReqBody reqBody = new ReqBody();
        ResponseEntity<ReqBody> resp = testRestTemplate.exchange(RequestEntity.post("/test").header("foo", "test").body(reqBody), ReqBody.class);
        log.info("result : {}", resp);
        assertThat(resp.getBody().getFoo(), is("test"));
    }
}
