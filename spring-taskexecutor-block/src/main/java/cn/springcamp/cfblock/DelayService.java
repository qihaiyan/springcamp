package cn.springcamp.cfblock;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DelayService {
    @Async
    public CompletableFuture<String> delayFoo(String v) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(v + " runs in thread: " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(v);
    }
}
