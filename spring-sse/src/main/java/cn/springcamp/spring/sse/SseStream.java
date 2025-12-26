package cn.springcamp.spring.sse;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static cn.springcamp.spring.sse.StreamStatus.ACTIVE;

public class SseStream {

    private final ImmutableSseEmitter emitter;
    private final ExecutorService executorService;
    private final ReentrantLock statusLock;
    private volatile StreamStatus streamStatus;
    private final ArrayBlockingQueue<Runnable> blockingQueue;

    SseStream(SseStreamBuilder sseChannelImplBuilder) {
        this.emitter = sseChannelImplBuilder.emitter;
        this.statusLock = new ReentrantLock();
        this.streamStatus = ACTIVE;
        this.blockingQueue = new ArrayBlockingQueue<>(sseChannelImplBuilder.maxQueuedEvents);
        this.executorService =
                new ThreadPoolExecutor(0, 1,
                        sseChannelImplBuilder.keepAliveTime,
                        TimeUnit.SECONDS, this.blockingQueue,
                        Thread.ofVirtual().factory());

    }

    public static SseStreamBuilder builder() {
        return new SseStreamBuilder();
    }

    public CompletableFuture<Void> send(Object object) {
        return this.send(object, null);
    }

    public CompletableFuture<Void> send(Object object, MediaType mediaType) {
        if (this.isCompleted())
            throw new RuntimeException(); //Check first, worst case two threads still try to acquire the lock, best case no lock is acquired
        this.statusLock.lock();
        try {
            if (this.isCompleted()) throw new RuntimeException();
            return CompletableFuture.runAsync(() -> {
                try {
                    this.emitter.send(object, mediaType);
                } catch (IOException | RejectedExecutionException e) {
                    throw new CompletionException(new RuntimeException(e));
                }
            }, this.executorService);
        } finally {
            this.statusLock.unlock();
        }
    }

    public void complete() {
        if (this.isCompleted()) return;
        this.markCompleted();
        this.executorService.close();
        this.emitter.complete();
    }

    public void completeWithError(Throwable ex) {
        if (this.isCompleted()) return;
        this.markCompleted();
        this.executorService.close();
        this.emitter.completeWithError(ex);
    }

    public SseEmitter getEmitter() {
        return this.emitter;
    }

    public int queueSize() {
        return this.blockingQueue.size();
    }

    private void markCompleted() {
        this.statusLock.lock();
        try {
            if (!this.isCompleted()) this.streamStatus = StreamStatus.COMPLETED;
        } finally {
            this.statusLock.unlock();
        }
    }

    public boolean isCompleted() {
        return this.streamStatus == StreamStatus.COMPLETED;
    }

}

class SseStreamBuilder {
    protected ImmutableSseEmitter emitter;
    protected long keepAliveTime; //In seconds
    protected int maxQueuedEvents;
    private long timeout;
    private Runnable onCompletionCallback;
    private Runnable onTimeoutCallback;
    private Consumer<Throwable> onErrorCallback;

    private final static String TIMEOUT_NEGATIVE_MESSAGE = "Sse timeout cannot be negative";
    private final static String KEEP_ALIVE_NEGATIVE_MESSAGE = "Sse stream queue keep alive time cannot be negative";
    private final static String MAX_QUEUED_EVENTS_NEGATIVE_MESSAGE = "Sse stream queue keep alive time cannot be negative";
    private final static long DEFAULT_TIMEOUT = 60_000L;


    SseStreamBuilder() {
        this.timeout = DEFAULT_TIMEOUT;
        this.keepAliveTime = 60;
        this.maxQueuedEvents = 100;
    }

    SseStreamBuilder(ImmutableSseEmitter emitter) {
        this();
        this.emitter = emitter;
    }


    public SseStreamBuilder onCompletion(Runnable callback) {
        this.onCompletionCallback = callback;
        return this;
    }

    public SseStreamBuilder onError(Consumer<Throwable> callback) {
        this.onErrorCallback = callback;
        return this;
    }

    public SseStreamBuilder onTimeout(Runnable callback) {
        this.onTimeoutCallback = callback;
        return this;
    }

    public SseStreamBuilder withTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }


    public SseStreamBuilder threadKeepAliveTime(long timeInSeconds) {
        this.keepAliveTime = timeInSeconds;
        return this;
    }

    public SseStreamBuilder maxQueuedEvents(int max) {
        this.maxQueuedEvents = max;
        return this;
    }


    public SseStream fromEmitter(ImmutableSseEmitter emitter) {
        this.emitter = emitter;
        return new SseStream(this);
    }


    public SseStream build() {
        if (this.emitter == null) this.emitter = new ImmutableSseEmitter(this.timeout);
        final var stream = new SseStream(this);
        this.configureCallback(stream);
        return stream;
    }


    private void configureCallback(SseStream stream) {
        this.emitter.onTimeout(() -> {
            stream.complete();
            if (this.onTimeoutCallback != null) this.onTimeoutCallback.run();
        });

        this.emitter.onCompletion(() -> {
            stream.complete();
            if (this.onCompletionCallback != null) this.onCompletionCallback.run();
        });

        this.emitter.onError(ex -> {
            stream.completeWithError(ex);
            if (this.onErrorCallback != null) this.onErrorCallback.accept(ex);
        });
    }

}

enum StreamStatus {
    ACTIVE,
    COMPLETED
}


