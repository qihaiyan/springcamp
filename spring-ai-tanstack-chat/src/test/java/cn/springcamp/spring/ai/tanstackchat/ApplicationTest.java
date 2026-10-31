package cn.springcamp.spring.ai.tanstackchat;

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
    void chatStreamsAgUiEvents() {
        when(chatModel.getOptions()).thenReturn(DeepSeekChatOptions.builder().build());
        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.just(
                chatResponse("你好"), chatResponse("，世界")));

        client.post().uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"threadId":"t1","runId":"r1","messages":[
                          {"id":"m1","role":"user","content":"你好"}]}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectBody(String.class)
                .value(body -> {
                    // 事件顺序：RUN_STARTED -> TEXT_MESSAGE_START -> 逐 token 的 TEXT_MESSAGE_CONTENT -> TEXT_MESSAGE_END -> RUN_FINISHED
                    assertThat(body.indexOf("RUN_STARTED")).isLessThan(body.indexOf("TEXT_MESSAGE_START"));
                    assertThat(body).contains("\"threadId\":\"t1\"");
                    assertThat(body).contains("\"role\":\"assistant\"");
                    assertThat(body).contains("\"delta\":\"你好\"");
                    assertThat(body).contains("\"delta\":\"，世界\"");
                    assertThat(body).contains("TEXT_MESSAGE_END");
                    assertThat(body).contains("RUN_FINISHED");
                    // JSON 转义：token 中的引号必须转义后才能作为 data: 行传输
                    assertThat(body).doesNotContain("\"delta\":\"未转义\"");
                });
    }

    @Test
    void multiTurnHistoryIsForwardedToModel() {
        when(chatModel.getOptions()).thenReturn(DeepSeekChatOptions.builder().build());
        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.just(chatResponse("好")));

        client.post().uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"threadId":"t1","runId":"r2","messages":[
                          {"id":"m1","role":"user","content":"第一轮问题"},
                          {"id":"m2","role":"assistant","content":"第一轮回答"},
                          {"id":"m3","role":"user","content":"第二轮问题"}]}
                        """)
                .exchange()
                .expectStatus().isOk();

        // AG-UI 每轮携带完整历史，服务端把 user/assistant 消息原样转给大模型（ChatClient 另拼入了默认 system 消息）
        var prompt = org.mockito.Mockito.mockingDetails(chatModel).getInvocations().stream()
                .filter(i -> i.getMethod().getName().equals("stream"))
                .map(i -> (Prompt) i.getArgument(0))
                .findFirst().orElseThrow();
        var history = prompt.getInstructions().stream()
                .filter(m -> m.getMessageType() != org.springframework.ai.chat.messages.MessageType.SYSTEM)
                .toList();
        assertThat(history).hasSize(3);
        assertThat(history.get(1).getText()).isEqualTo("第一轮回答");
        assertThat(history.get(2).getText()).isEqualTo("第二轮问题");
    }

    @Test
    void upstreamErrorEmitsRunErrorEvent() {
        when(chatModel.getOptions()).thenReturn(DeepSeekChatOptions.builder().build());
        when(chatModel.stream(any(Prompt.class))).thenReturn(Flux.error(new RuntimeException("boom")));

        client.post().uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"threadId":"t1","runId":"r3","messages":[
                          {"id":"m1","role":"user","content":"你好"}]}
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("RUN_ERROR").contains("boom"));
    }

    private static ChatResponse chatResponse(String text) {
        return new ChatResponse(List.of(new Generation(new AssistantMessage(text))));
    }
}
