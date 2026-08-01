package cn.springcamp.springboot.datetimetoepoch;

import cn.springcamp.springboot.datetimetoepoch.data.MyDomain;
import cn.springcamp.springboot.datetimetoepoch.data.MyDomainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTest {

    @Autowired
    private MyDomainRepository myDomainRepository;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void before() {
        restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void testDbCallRest() {
        MyDomain myDomain = new MyDomain();
        myDomain.setName("test");
        LocalDateTime now = LocalDateTime.now();
        myDomain.setCreateTime(now);
        myDomain = myDomainRepository.save(myDomain);
        String strResp = restClient.get().uri("/db").retrieve().body(String.class);
        System.out.println("db strResp : " + strResp);
        assertThat(strResp.contains(String.valueOf(now.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond())), is(true));
    }
}
