package cn.springcamp.spring.ai.deepseek;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;
    private final DeepSeekChatModel chatModel;
    private final WeatherService weatherService;
    private final ToolCallRecorder toolCallRecorder;

    public ChatController(ChatClient chatClient, DeepSeekChatModel chatModel, WeatherService weatherService,
                          ToolCallRecorder toolCallRecorder) {
        this.chatClient = chatClient;
        this.chatModel = chatModel;
        this.weatherService = weatherService;
        this.toolCallRecorder = toolCallRecorder;
    }

    @GetMapping(value = "/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatClient.prompt().user(message).call().content();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public Flux<String> stream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return chatClient.prompt().user(message).stream().content();
    }

    @GetMapping("/reasoning")
    public Map<String, String> reasoning(@RequestParam(value = "message", defaultValue = "9.11 和 9.8 哪个大？") String message) {
        ChatResponse response = chatModel.call(new Prompt(message));
        DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) Objects.requireNonNull(response.getResult()).getOutput();
        return Map.of(
                "reasoning", output.getReasoningContent() != null ? output.getReasoningContent() : "",
                "answer", output.getText() != null ? output.getText() : ""
        );
    }

    @GetMapping(value = "/tool")
    public Map<String, Object> tool(@RequestParam(value = "message", defaultValue = "北京和上海今天天气怎么样？") String message) {
        String answer = chatClient.prompt().user(message).tools(weatherService).call().content();
        return Map.of(
                "answer", answer != null ? answer : "",
                "toolCalls", toolCallRecorder.get()
        );
    }
}
