package cn.springcamp.spring.ai.htmxchat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 聊天接口：
 * <ul>
 *   <li>POST /chat   —— 一次性返回 text/event-stream：第一条消息是「用户气泡 + AI 气泡骨架」
 *       的 HTML 片段，之后每个 token 一条消息，内容是 hx-partial 片段（整段替换 AI 气泡文本）。
 *       htmx 4 的 sse 扩展会把每条消息当作一次局部交换，实现流式打字效果</li>
 *   <li>DELETE /chat —— 清空指定会话的服务端记忆</li>
 * </ul>
 */
@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final TemplateEngine templateEngine;

    public ChatController(ChatClient chatClient, ChatMemory chatMemory, TemplateEngine templateEngine) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.templateEngine = templateEngine;
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<ServerSentEvent<String>> chat(@RequestParam String message, @RequestParam String conversationId) {
        String answerId = "ans-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Context context = new Context(null, Map.of("message", message, "answerId", answerId));
        // 用 markup selector 渲染 chat.html 中的 exchange 片段（用户气泡 + AI 气泡骨架）
        String skeleton = templateEngine.process("fragments/chat", Set.of("div.exchange"), context);

        StringBuilder answer = new StringBuilder();
        return Flux.concat(
                // 第 1 条 SSE 消息：主交换内容（beforeend 追加进消息列表），AI 气泡里先显示打字动画
                Flux.just(textEvent(skeleton)),
                // 后续消息：每个 token 都整段替换 AI 气泡文本，第一个 token 到达时打字动画自然消失
                chatClient.prompt()
                        .user(message)
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                        .stream()
                        .content()
                        // SSE 数据会被 htmx 当作 HTML 换入页面，必须先做 HTML 转义
                        .map(ChatController::escapeHtml)
                        .onErrorResume(ex -> Flux.just("\n\n⚠️ AI 调用失败：" + escapeHtml(rootMessage(ex))))
                        .doOnNext(answer::append)
                        .map(token -> partialEvent(answerId, answer.toString())));
    }

    @DeleteMapping
    @ResponseBody
    public void clear(@RequestParam String conversationId) {
        chatMemory.clear(conversationId);
    }

    private static ServerSentEvent<String> textEvent(String html) {
        return ServerSentEvent.<String>builder().data(html).build();
    }

    private static ServerSentEvent<String> partialEvent(String answerId, String html) {
        return ServerSentEvent.<String>builder()
                .data("<hx-partial hx-target=\"#" + answerId + "\" hx-swap=\"innerHTML\">" + html + "</hx-partial>")
                .build();
    }

    private static String escapeHtml(String text) {
        return StringUtils.hasText(text) ? text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;") : text;
    }

    private static String rootMessage(Throwable ex) {
        while (ex.getCause() != null) {
            ex = ex.getCause();
        }
        return StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : ex.getClass().getSimpleName();
    }
}
