package cn.springcamp.spring.ai.tanstackchat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


import java.util.List;
import java.util.UUID;

/**
 * 聊天接口：POST /api/chat，说 AG-UI 协议（TanStack AI 客户端的标准线协议）。
 * <p>请求体是 AG-UI {@code RunAgentInput}：{@code {threadId, runId, messages:[{id,role,content}]}}，
 * 客户端每轮都会携带完整对话历史，服务端只做无状态的流式转发。</p>
 * <p>响应是 text/event-stream，每个 token 一条 {@code data:} 事件：</p>
 * <pre>
 * data:{"type":"RUN_STARTED","threadId":"...","runId":"..."}
 * data:{"type":"TEXT_MESSAGE_START","messageId":"...","role":"assistant"}
 * data:{"type":"TEXT_MESSAGE_CONTENT","messageId":"...","delta":"你"}
 * data:{"type":"TEXT_MESSAGE_END","messageId":"..."}
 * data:{"type":"RUN_FINISHED","threadId":"...","runId":"..."}
 * </pre>
 */
@RestController
public class ChatController {


    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping(value = "/api/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chat(@RequestBody RunAgentInput input) {
        String messageId = "msg-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String threadId = StringUtils.hasText(input.threadId()) ? input.threadId() : "";
        String runId = StringUtils.hasText(input.runId()) ? input.runId() : "";

        Flux<ServerSentEvent<String>> header = Flux.just(
                event(AgUiEvent.runStarted(threadId, runId)),
                event(AgUiEvent.textMessageStart(messageId)));

        Flux<ServerSentEvent<String>> tokens = chatClient.prompt()
                .messages(toSpringMessages(input.messages()))
                .stream()
                .content()
                .map(delta -> event(AgUiEvent.textMessageContent(messageId, delta)));

        Flux<ServerSentEvent<String>> tail = Flux.just(
                event(AgUiEvent.textMessageEnd(messageId)),
                event(AgUiEvent.runFinished(threadId, runId)));

        // 中途出错时补一个 RUN_ERROR 终止事件，客户端据此结束本轮并展示错误
        return Flux.concat(header, tokens, tail)
                .onErrorResume(ex -> Flux.just(event(AgUiEvent.runError(rootMessage(ex)))));
    }

    /** AG-UI 客户端（useChat）每轮 POST 的请求体 */
    record RunAgentInput(String threadId, String runId, List<WireMessage> messages) {
        record WireMessage(String id, String role, String content) {
        }
    }

    /** AG-UI wire message（id/role/content）转 Spring AI Message，只保留 user/assistant，最多回看 20 条 */
    static List<Message> toSpringMessages(List<RunAgentInput.WireMessage> messages) {
        if (messages == null) {
            return List.of();
        }
        return messages.stream()
                .skip(Math.max(0, messages.size() - 20))
                .map(m -> "assistant".equals(m.role())
                        ? (Message) new AssistantMessage(m.content())
                        : (Message) new UserMessage(m.content()))
                .toList();
    }

    private static ServerSentEvent<String> event(String json) {
        return ServerSentEvent.<String>builder().data(json).build();
    }

    /** AG-UI 事件的 JSON 组装（字段全是短字符串，手写拼接足够，也让线格式一目了然） */
    static final class AgUiEvent {

        static String runStarted(String threadId, String runId) {
            return "{\"type\":\"RUN_STARTED\",\"threadId\":\"" + esc(threadId) + "\",\"runId\":\"" + esc(runId) + "\"}";
        }

        static String textMessageStart(String messageId) {
            return "{\"type\":\"TEXT_MESSAGE_START\",\"messageId\":\"" + esc(messageId) + "\",\"role\":\"assistant\"}";
        }

        static String textMessageContent(String messageId, String delta) {
            return "{\"type\":\"TEXT_MESSAGE_CONTENT\",\"messageId\":\"" + esc(messageId) + "\",\"delta\":\"" + esc(delta) + "\"}";
        }

        static String textMessageEnd(String messageId) {
            return "{\"type\":\"TEXT_MESSAGE_END\",\"messageId\":\"" + esc(messageId) + "\"}";
        }

        static String runFinished(String threadId, String runId) {
            return "{\"type\":\"RUN_FINISHED\",\"threadId\":\"" + esc(threadId) + "\",\"runId\":\"" + esc(runId) + "\"}";
        }

        static String runError(String message) {
            return "{\"type\":\"RUN_ERROR\",\"message\":\"" + esc(message) + "\",\"code\":\"UPSTREAM_ERROR\"}";
        }

        private static String esc(String s) {
            return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b")
                    .replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
        }
    }

    private static String rootMessage(Throwable ex) {
        while (ex.getCause() != null) {
            ex = ex.getCause();
        }
        return StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : ex.getClass().getSimpleName();
    }
}
