package cn.springcamp.concurrency;

class Synchronized {
    private int counter0;

    void increment() {
        synchronized (this) {
            counter0++;
        }
    }
}

class SynchronizedMethod {
    private int counter0;

    synchronized void increment() {
        counter0++;
    }
}