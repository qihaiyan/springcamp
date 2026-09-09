package cn.springcamp.spring.ai.htmxchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTest {

    @LocalServerPort
    int port;

    /** mock 掉大模型，测试不依赖真实的 DeepSeek API Key */
    @MockitoBean
    DeepSeekChatModel chatModel;

    WebTestClient client;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .responseTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Test
    void indexPageLoads() {
        client.get().uri("/").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertThat(html).contains("htmx.org@4.0.0");
                    assertThat(html).contains("alpinejs@3.17.2");
                    assertThat(html).contains("hx-ext=\"sse\"");
                    assertThat(html).contains("hx-post=\"/chat\"");
                    assertThat(html).contains("x-data=\"chatApp()\"");
                });
    }

    @Test
    void chatStreamsSkeletonThenPartialEvents() {
        when(chatModel.getOptions()).thenReturn(DeepSeekChatOptions.builder().build());
        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.just(
                chatResponse("你好"), chatResponse("，世界")));

        client.post().uri("/chat")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("message", "你好").with("conversationId", "c1"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBody(String.class)
                .value(body -> {
                    // 第 1 条 SSE 消息：用户气泡 + 带唯一 id 和打字动画的 AI 气泡骨架
                    assertThat(body).contains("message user");
                    assertThat(body).contains("你好");
                    assertThat(body).contains("typing-dots");
                    assertThat(body).contains("id=\"ans-");
                    // 后续消息：hx-partial 整段替换 AI 气泡文本，内容逐 token 累积
                    assertThat(body).contains("hx-partial");
                    assertThat(body).contains("你好");
                    assertThat(body).contains("你好，世界");
                });
    }

    @Test
    void clearRemovesConversation() {
        client.delete().uri("/chat?conversationId=c1").exchange()
                .expectStatus().isOk();
    }

    private static ChatResponse chatResponse(String text) {
        return new ChatResponse(List.of(new Generation(new AssistantMessage(text))));
    }
}
