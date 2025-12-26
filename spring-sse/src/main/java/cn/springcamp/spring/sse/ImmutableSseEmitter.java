package cn.springcamp.spring.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ImmutableSseEmitter extends SseEmitter {
    private final AtomicBoolean onCompleteCalled;
    private final AtomicBoolean onErrorCalled;
    private final AtomicBoolean onTimeoutCalled;
    private final static String ERROR_MESSAGE = "Callbacks are managed by SseStream and cannot be modified. Configure callbacks via SseStreamBuilder instead.";

    public ImmutableSseEmitter() {
        super();
        this.onCompleteCalled = new AtomicBoolean(false);
        this.onErrorCalled = new AtomicBoolean(false);
        this.onTimeoutCalled = new AtomicBoolean(false);
    }

    public ImmutableSseEmitter(Long timeout) {
        super(timeout);
        this.onCompleteCalled = new AtomicBoolean(false);
        this.onErrorCalled = new AtomicBoolean(false);
        this.onTimeoutCalled = new AtomicBoolean(false);
    }

    public void onError(Consumer<Throwable> callback) {
        if (!this.onErrorCalled.compareAndSet(false, true)) throw new RuntimeException(ERROR_MESSAGE);
        super.onError(callback);
    }

    public void onTimeout(Runnable callback) {
        if (!this.onTimeoutCalled.compareAndSet(false, true)) throw new RuntimeException(ERROR_MESSAGE);
        super.onTimeout(callback);
    }

    public void onCompletion(Runnable callback) {
        if (!this.onCompleteCalled.compareAndSet(false, true)) throw new RuntimeException(ERROR_MESSAGE);
        super.onCompletion(callback);
    }
}

