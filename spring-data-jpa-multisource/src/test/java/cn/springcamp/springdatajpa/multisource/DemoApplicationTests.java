package cn.springcamp.springdatajpa.multisource;

import cn.springcamp.springdatajpa.multisource.service.DbService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private DbService dbService;

    @Test
    void test() {
        String msg = dbService.getHelloMessage();
        assertThat(msg, is("Hello World : primary's value = primary , other's value = other"));
    }

}
