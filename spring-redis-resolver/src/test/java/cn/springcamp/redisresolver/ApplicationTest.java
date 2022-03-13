package cn.springcamp.redisresolver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private DemoRepository demoRepository;

    @Test
    public void cacheTest() {
        DemoEntity demoEntity = demoRepository.findById(1L).orElse(null);
        assertThat(demoEntity == null, is(true));

        demoEntity = new DemoEntity();
        demoEntity.setId(1L);
        demoRepository.save(demoEntity);
        demoEntity = demoRepository.findById(1L).orElse(new DemoEntity());
        assertThat(demoEntity.getId(), is(1L));
    }
}
