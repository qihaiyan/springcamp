package cn.springcamp.spring.sse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executors;

@RestController
public class SseController {
    public record TestEvent(int id, String name, String content) {
    }

    private final SseRegistry<Object, Object> registry;

    public SseController() {
        this.registry = SseRegistry.builder()
                .maxStreams(100)
                .maxEvents(100)
                .timeout(60_000L)
                .build();
    }

    @GetMapping("/sse-mock")
    public SseEmitter streamData() {
        SseEmitter emitter = new SseEmitter();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    emitter.send("Message " + i, MediaType.TEXT_PLAIN);
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter;
    }

    @GetMapping("/stream/{id}")
    public SseEmitter subscribe(@PathVariable String id) {
        SseStream stream = registry.createAndRegister(id);
        return stream.getEmitter();
    }

    @PostMapping("/broadcast")
    public void broadcast(@RequestBody TestEvent event) {
        registry.broadcast(event);
    }

    @PostMapping("/send/{id}")
    public void sendTo(@PathVariable String id, @RequestBody TestEvent event) {
        registry.sendTo(id, event);
    }
}
