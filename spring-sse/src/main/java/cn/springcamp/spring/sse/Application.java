package cn.springcamp.spring.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
@RestController
public class Application {

    @GetMapping("/sse-mock")
    public SseEmitter streamData() {
        SseEmitter emitter = new SseEmitter();
        Executors.newCachedThreadPool().execute(() -> {
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

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
