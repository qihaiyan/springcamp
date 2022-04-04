package cn.springcamp.springboot.datetimetoepoch;

import cn.springcamp.springboot.datetimetoepoch.data.MyDomain;
import cn.springcamp.springboot.datetimetoepoch.data.MyDomainRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MyDomainRepository myDomainRepository;

    @Test
    public void testDbCallRest() {
        MyDomain myDomain = new MyDomain();
        myDomain.setName("test");
        LocalDateTime now = LocalDateTime.now();
        myDomain.setCreateTime(now);
        myDomain = myDomainRepository.save(myDomain);
        String strResp = testRestTemplate.getForObject("/db", String.class);
        System.out.println("db strResp : " + strResp);
        assertThat(strResp.contains(String.valueOf(now.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond())), is(true));
    }
}
