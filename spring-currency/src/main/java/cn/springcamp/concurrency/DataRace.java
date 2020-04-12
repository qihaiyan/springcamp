package cn.springcamp.concurrency;

public class DataRace {

    public static void main(String[] args) throws InterruptedException {
        Waiter waiter = new Waiter();
        Thread waiterThread = new Thread(waiter);
        waiterThread.start();
        Thread.sleep(10L);  // 延迟10毫秒后再调用finish方法，会发现程序会一直运行不退出，在run方法中shouldFinish一直是false
        waiter.finish();
        waiterThread.join();
    }
}

class Waiter implements Runnable {
    private volatile boolean shouldFinish;

    void finish() {
        shouldFinish = true;
    }

    public void run() {
        long iteration = 0;
        while (!shouldFinish) {
            iteration++;
        }

        System.out.println("Finished after: " + iteration);
    }
}