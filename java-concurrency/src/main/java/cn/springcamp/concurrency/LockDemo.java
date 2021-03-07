package cn.springcamp.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class LockDemo {
    private final Lock lock = new ReentrantLock();
    private int counter0;

    public static void main(String[] args) {
        LockDemo lockDemo = new LockDemo();
        lockDemo.increment();
        System.out.println("count is: " + lockDemo.getCounter0());
    }

    public int getCounter0() {
        return counter0;
    }

    void increment() {
        lock.lock();
        try {
            counter0++;
        } finally {
            lock.unlock();
        }

    }
}

class ReadWriteLockDemo {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int counter1;

    void increment() {
        lock.writeLock().lock();
        try {
            counter1++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    int current() {
        lock.readLock().lock();
        try {
            return counter1;
        } finally {
            lock.readLock().unlock();
        }
    }
}
