package cn.springcamp.concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CountDownLatch latch = new CountDownLatch(1);
        Receiver receiver = new Receiver(latch);
        executorService.submit(receiver);
        latch.await();
        System.out.println("latch done");
        executorService.shutdown();
    }
}

class Receiver implements Runnable {

    private CountDownLatch latch;

    public Receiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        latch.countDown();
    }
}
