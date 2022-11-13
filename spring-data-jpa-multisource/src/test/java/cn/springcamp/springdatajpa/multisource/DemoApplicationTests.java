package cn.springcamp.springdatajpa.multisource;

import cn.springcamp.springdatajpa.multisource.service.DbService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private DbService dbService;

    @Test
    public void test() {
        String msg = dbService.getHelloMessage();
        assertThat(msg, is("Hello World : primary's value = primary , other's value = other"));
    }

}
