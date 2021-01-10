package com.example.cfblock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

@SpringBootApplication
@EnableAsync
public class CfblockApplication implements CommandLineRunner {

    @Autowired
    private Executor taskExecutor;
    @Autowired
    private DelayService delayService;

    public static void main(String[] args) {
        SpringApplication.run(CfblockApplication.class, args);
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    @Override
    public void run(String... args) {
        while (true) {
            try {
                CompletableFuture.runAsync(() -> CompletableFuture.allOf(Stream.of("1", "2", "3")
                        .map(v -> delayService.delayFoo(v))
                        .toArray(CompletableFuture[]::new))
                        .join(), taskExecutor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
