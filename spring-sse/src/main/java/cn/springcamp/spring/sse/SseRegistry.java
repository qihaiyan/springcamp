package cn.springcamp.spring.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

@AllArgsConstructor
@Builder
public class SseRegistry<ID, E> {
    @Builder.Default
    private final Map<ID, SseStream> streamRegistry = new ConcurrentHashMap<>();
    @Builder.Default
    final ExecutorService registryExecutor = Executors.newVirtualThreadPerTaskExecutor();
    @Builder.Default
    private final ReentrantLock lifeCycleLock = new ReentrantLock();
    @Builder.Default
    private final long timeout = 60_000L;
    @Builder.Default
    private final int maxStreams = 20;
    @Builder.Default
    private final int maxEvents = 20;
    @Builder.Default
    private final int maxQueuedEventsPerStream = 50;
    private final EventHistory<E> eventRegistry;
    @Builder.Default
    private final long threadKeepAliveTime = 1L;
    private final Runnable onStreamComplete;
    private final Runnable onStreamTimeout;
    private final Consumer<Throwable> onStreamError;
    @Builder.Default
    private final EventEvictionPolicy eventEvictionPolicy = EventEvictionPolicy.FIFO;
    @Builder.Default
    private final Predicate<E> eventPredicate = e -> true;
    @Builder.Default
    private volatile RegistryStatus status = RegistryStatus.ACTIVE;

    private static final String NULL_EVENT_MESSAGE = "Event cannot be null";
    private static final String NULL_ID_MESSAGE = "Id cannot be null";
    private static final String NULL_STREAM_MESSAGE = "Sse Stream cannot be null";
    private static final String COMPLETED_STREAM_MESSAGE = "Cannot add completed stream to sse registry";

    public static class SseRegistryBuilder<ID, E> {
        public SseRegistryBuilder<ID, E> maxEvents(int maxEvents) {
            this.eventRegistry = new EventHistory<>(maxEvents);
            return this;
        }
    }

    public SseStream createAndRegister(ID id) {
        if (this.isShutdown()) throw new RuntimeException();
        this.lifeCycleLock.lock();
        try {
            if (this.isShutdown()) throw new RuntimeException();
            if (this.streamRegistry.size() >= this.maxStreams) throw new RuntimeException();
            final var newStream = this.createStream(id);
            final var oldStream = this.streamRegistry.put(id, newStream);
            if (oldStream != null)
                this.registryExecutor.execute(oldStream::complete);
            return newStream;
        } finally {
            lifeCycleLock.unlock();
        }
    }

    public SseStream register(ID id, SseStream stream) {
        if (this.isShutdown()) throw new RuntimeException();
        this.lifeCycleLock.lock();
        try {
            if (this.isShutdown()) throw new RuntimeException();
            if (this.size() >= this.maxStreams) throw new RuntimeException();
            final var oldStream = this.streamRegistry.put(id, stream);
            if (oldStream != null)
                this.registryExecutor.execute(oldStream::complete);
            return stream;
        } finally {
            this.lifeCycleLock.unlock();
        }
    }

    public SseStream get(ID id) {
        if (this.isShutdown()) throw new RuntimeException();
        return this.streamRegistry.get(id);
    }

    public void remove(ID id) {
        if (this.isShutdown()) return;
        this.lifeCycleLock.lock();
        try {
            if (this.isShutdown()) return;
            final var stream = this.streamRegistry.remove(id);
            if (stream != null) this.registryExecutor.execute(stream::complete);
        } finally {
            this.lifeCycleLock.unlock();
        }
    }

    public int size() {
        if (this.isShutdown()) return 0;
        return this.streamRegistry.size();
    }

    public CompletableFuture<Void> broadcast(E event) {
        return this.broadcast(event, id -> true);
    }

    public CompletableFuture<Void> broadcast(E event, Predicate<ID> idPredicate) {
        if (this.isShutdown())
            throw new RuntimeException();
        this.registerEvent(event);
        final var futures = new ArrayList<CompletableFuture<Void>>();
        this.streamRegistry.forEach((id, s) -> futures.add(CompletableFuture.runAsync(() -> {
            if (idPredicate.test(id)) {
                this.sendWithoutId(s, event);
            }
        }, this.registryExecutor)));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    public boolean sendTo(ID id, E event) {
        return this.send(id, event);
    }

    public CompletableFuture<Void> shutdown() {
        if (this.isShutdown()) return CompletableFuture.completedFuture(null);

        this.lifeCycleLock.lock();
        try {
            if (this.isShutdown()) return CompletableFuture.completedFuture(null);
            this.status = RegistryStatus.SHUTDOWN;
        } finally {
            this.lifeCycleLock.unlock();
        }

        final var futures = new ArrayList<CompletableFuture<Void>>(this.streamRegistry.size());
        this.streamRegistry.forEach((id, s) -> {
            final var c = CompletableFuture.runAsync(s::complete, this.registryExecutor);
            futures.add(c);
        });
        futures.add(CompletableFuture.runAsync(this.registryExecutor::close));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    public boolean isShutdown() {
        return this.status == RegistryStatus.SHUTDOWN;
    }

    private void sendWithoutId(SseStream stream, E event) {
        try {
            if (this.isShutdown()) return;
            this.registerEvent(event);
            if (stream == null) return;
            stream.send(event);
        } catch (RuntimeException ignored) {
        }
    }

    private boolean send(ID id, E event) {
        try {
            if (this.isShutdown()) return false;
            this.registerEvent(event);

            final var stream = this.get(id);
            if (stream == null) return false;
            stream.send(event);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private SseStream createStream(ID id) {
        final Runnable onComplete = () -> {
            this.streamRegistry.remove(id);
            if (this.onStreamComplete != null) this.onStreamComplete.run();
        };

        final Runnable onTimeout = () -> {
            this.streamRegistry.remove(id);
            if (this.onStreamTimeout != null) this.onStreamTimeout.run();
        };

        final Consumer<Throwable> onError = e -> {
            this.streamRegistry.remove(id);
            if (this.onStreamError != null) this.onStreamError.accept(e);
        };


        return SseStream.builder()
                .withTimeout(this.timeout)
                .onCompletion(onComplete)
                .onError(onError)
                .onTimeout(onTimeout)
                .maxQueuedEvents(this.maxQueuedEventsPerStream)
                .threadKeepAliveTime(this.threadKeepAliveTime)
                .build();
    }

    public void registerEvent(E event) {
        if (this.isShutdown()) throw new RuntimeException();
        this.eventRegistry.add(event, this.eventEvictionPolicy, this.eventPredicate);
    }

    protected Collection<SseStream> getAllStreams() {
        return this.streamRegistry.values();
    }
}

enum RegistryStatus {
    ACTIVE,
    SHUTDOWN
}

