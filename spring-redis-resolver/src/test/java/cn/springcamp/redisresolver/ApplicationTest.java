package cn.springcamp.redisresolver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.is;

@Import(TestRedisConfiguration.class)
@SpringBootTest
class ApplicationTest {

    @Autowired
    private DemoRepository demoRepository;

    @Test
    void cacheTest() {
        DemoEntity demoEntity = demoRepository.findById(1L).orElse(null);
        assertThat(demoEntity == null, is(true));

        demoEntity = new DemoEntity();
        demoEntity.setId(1L);
        demoRepository.save(demoEntity);
        demoEntity = demoRepository.findById(1L).orElse(new DemoEntity());
        assertThat(demoEntity.getId(), is(1L));
    }
}
