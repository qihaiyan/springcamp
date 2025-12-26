package cn.springcamp.spring.sse;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

@Builder
@AllArgsConstructor
public final class EventReplayer<ID, E> {
    private final ExecutorService executorService;
    private final List<E> events;
    private final Map<ID, SseStream> streams;
    @Builder.Default
    private final int from = -1;
    @Builder.Default
    private final int to = -1;
    private final boolean all;
    private final Predicate<E> matching;
    private static final String NULL_ID_MESSAGE = "Id cannot be null";

    public CompletableFuture<Void> to(ID id) {
        final var stream = this.streams.get(id);
        if (stream == null) return CompletableFuture.failedFuture(new NullPointerException());
        return stream.send(this.eventsToSend());
    }


    public CompletableFuture<Void> toAll() {
        return this.toAllMatching(p -> true);
    }

    public CompletableFuture<Void> toAllMatching(Predicate<ID> idPredicate) {
        final var futures = new ArrayList<CompletableFuture<Void>>();
        final var events = this.eventsToSend();
        streams.forEach((id, s) -> futures.add(CompletableFuture.runAsync(() -> {
            if (idPredicate.test(id)) {
                s.send(events);
            }
        }, this.executorService)));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }


    private List<E> eventsToSend() {
        if (this.all) return this.events;
        else if (this.matching != null) return this.getMatchingEvents();
        else if (this.from != -1 && this.to == -1) return this.events.subList(from, this.events.size());
        else return this.events.subList(from, to);
    }

    private List<E> getMatchingEvents() {
        return this.events.stream().filter(this.matching).toList();
    }
}
