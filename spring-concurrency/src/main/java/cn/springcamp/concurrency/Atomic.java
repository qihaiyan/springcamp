package cn.springcamp.concurrency;

import java.util.concurrent.atomic.AtomicBoolean;

class Atomic {

    public static void main(String[] args) {
        AtomicRun atomicRun = new AtomicRun();
        Thread waiterThread1 = new Thread(atomicRun);
        Thread waiterThread2 = new Thread(atomicRun);
        waiterThread1.start();
        waiterThread2.start();
    }
}

class AtomicRun implements Runnable {
    private final AtomicBoolean shouldFinish = new AtomicBoolean(false);

    public void run() {
        if (shouldFinish.compareAndSet(false, true)) {
            System.out.println("initialized only once");
        }
    }
}