package cn.springcamp.spring.sse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

final class EventHistory<E> {
    private final ArrayList<E> events;
    private final ReentrantLock lock;
    private final int maxSize;
    private final static String ERR_MESSAGE = "Event history max size of %s has been reached.";

    public EventHistory(int maxSize) {
        this.maxSize = maxSize;
        this.events = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public void add(E event, EventEvictionPolicy policy, Predicate<E> eventPredicate) {
        this.lock.lock();
        try {
            if (eventPredicate != null && !eventPredicate.test(event)) {
                return;
            }

            if (this.events.size() >= this.maxSize) {
                switch (policy) {
                    case STRICT -> throw new RuntimeException(
                            ERR_MESSAGE.formatted(this.maxSize)
                    );
                    case FIFO -> this.events.removeFirst();
                    case LIFO -> this.events.removeLast();
                }
            }

            this.events.add(event);

        } finally {
            lock.unlock();
        }
    }

    public void remove(E event){
        if(this.events.isEmpty()) return;
        this.lock.lock();
        try {
            if (this.events.isEmpty()) return;
            this.events.remove(event);
        }finally {
            this.lock.unlock();
        }
    }



    public List<E> getAll() {
        if (this.events.isEmpty()) return List.of();
        lock.lock();
        try {
            return Collections.unmodifiableList(this.events);
        } finally {
            lock.unlock();
        }
    }

}
