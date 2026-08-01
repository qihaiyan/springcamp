package cn.springcamp.springcache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestRedisConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void paramGreetingShouldReturnTailoredMessage() throws Exception {

        this.mockMvc.perform(get("/greeting?id=1").contentType(MediaType.APPLICATION_FORM_URLENCODED).content("p1=test"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello, 你好!"));
    }
}

