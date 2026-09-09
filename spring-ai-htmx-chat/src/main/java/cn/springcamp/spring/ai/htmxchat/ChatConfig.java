package cn.springcamp.spring.ai.htmxchat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory() {
        // 内存中的会话记忆，保留每个会话最近 20 条消息
        return MessageWindowChatMemory.builder().maxMessages(20).build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory, Environment env) {
        ChatClient.Builder target = env.acceptsProfiles(Profiles.of("demo"))
                ? ChatClient.builder(demoChatModel())   // demo 模式：无需 API Key 的本地假模型
                : builder;
        return target
                .defaultSystem("你是 springcamp 示例项目的 AI 助手，请用简洁准确的中文回答问题。")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * demo 模式使用的假模型：把一段固定回复按字符逐个吐出，模拟真实的流式输出。
     * 启动方式：./gradlew bootRun --args='--spring.profiles.active=demo'
     */
    private ChatModel demoChatModel() {
        return new ChatModel() {
            @Override
            public ChatResponse call(Prompt prompt) {
                return chatResponse(demoReply(prompt));
            }

            @Override
            public Flux<ChatResponse> stream(Prompt prompt) {
                String reply = demoReply(prompt);
                return Flux.fromStream(reply.codePoints().mapToObj(cp -> new String(Character.toChars(cp))))
                        .delayElements(Duration.ofMillis(40))
                        .map(ChatConfig::chatResponse);
            }
        };
    }

    private static String demoReply(Prompt prompt) {
        String userText = prompt.getInstructions().stream()
                .filter(m -> m.getMessageType() == MessageType.USER)
                .map(Message::getText)
                .collect(Collectors.joining("\n"));
        return "（演示模式，无需 API Key）收到你的消息：「" + userText + "」。\n\n"
                + "当前界面由 Spring AI + HTMX + Alpine.js 组合实现：\n"
                + "1. 表单通过 htmx 发起 POST，服务端返回 Thymeleaf 渲染的 HTML 片段；\n"
                + "2. AI 气泡骨架上的 SSE 连接把回答逐字流入页面；\n"
                + "3. Alpine.js 负责输入框、发送按钮和打字动画等交互状态。\n\n"
                + "配置真实的 DeepSeek API Key 后，这里将是大模型的流式回答。";
    }

    private static ChatResponse chatResponse(String text) {
        return new ChatResponse(List.of(new Generation(new AssistantMessage(text))));
    }
}
