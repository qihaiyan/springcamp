package cn.springcamp.concurrency;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CompletableFutureDemo {

    static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        accept();
        apply();
        combine();
        compose();
        exceptional();
        long start = System.currentTimeMillis();
        streamComp();
        long stop = System.currentTimeMillis();
        System.out.println("cf time elapsed " + (stop - start));
        streamCompWrong();
        start = System.currentTimeMillis();
        parallelstream();
        stop = System.currentTimeMillis();
        System.out.println("parallelstream time elapsed " + (stop - start));
        executor.shutdown();
    }

    public static void accept() {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> CompletableFutureDemo.getPrice("accept"), executor);
        future.thenAccept(p -> {
            System.out.println("Executing in " + Thread.currentThread().getName() + ", price is: " + p);
        });
        future.thenAcceptAsync(p -> {
            System.out.println("Executing in " + Thread.currentThread().getName() + ", async price is: " + p);
        }, executor);
        delay();

    }

    public static void apply() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future = CompletableFuture
                .supplyAsync(() -> CompletableFutureDemo.getPrice("apply"));
        CompletableFuture<String> result = future.thenApply(p -> p + "1");
        future.whenComplete((p, ex) -> {
            System.out.println("Executing in " + Thread.currentThread().getName() + " ,price is: " + p);
        });
        System.out.println("Executing in " + Thread.currentThread().getName() + " ,price is: " + result.join());
    }

    public static void compose() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future1 = CompletableFuture
                .supplyAsync(() -> CompletableFutureDemo.getPrice("compose"));
        CompletableFuture<String> result = future1.thenCompose(i -> CompletableFuture.supplyAsync(() -> (i + "10")));

        System.out.println("Executing in " + Thread.currentThread().getName() + " ,compose price is: " + result.join());
    }

    public static void combine() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future1 = CompletableFuture.supplyAsync(() -> CompletableFutureDemo.getPrice("combine1"));
        CompletableFuture<Long> future2 = CompletableFuture.supplyAsync(() -> CompletableFutureDemo.getPrice("combine2"));
        CompletableFuture<Long> result = future1.thenCombine(future2, (f1, f2) -> f1 + f2);

        System.out.println("Executing in " + Thread.currentThread().getName() + " ,combine price is: " + result.join());
    }

    public static void exceptional() {
        CompletableFuture<Long> future1 = CompletableFuture.supplyAsync(() -> CompletableFutureDemo.getPrice("exception1"));
        CompletableFuture<Long> future2 = CompletableFuture
                .supplyAsync(() -> CompletableFutureDemo.getExcept("exception2"))
                // 出现异常时返回默认值，如果此处没有exceptionally处理，异常会在后续的join中抛出
                .exceptionally((ex) -> {
                    System.out.println("Executing in " + Thread.currentThread().getName() + ", get excetion " + ex);
                    return 0L;
                });
        CompletableFuture<Long> result = future1.thenCombine(future2, (f1, f2) -> f1 + f2);

        try {
            System.out.println("Executing in " + Thread.currentThread().getName() + " ,combine price is: " + result.join());
        } catch (CompletionException ex) {
            System.out.println("Executing in " + Thread.currentThread().getName() + " ,combine price error: " + ex);
        }
    }

    public static void streamComp() {
        long start = System.currentTimeMillis();
        List<Long> prices = Stream.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
                .map(p -> CompletableFuture.supplyAsync(() -> CompletableFutureDemo.getPrice("exception1"), executor))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        System.out.println("Executing in " + Thread.currentThread().getName() + " ,streamComp price is: " + prices);
        long stop = System.currentTimeMillis();
        System.out.println("2 collect time elapsed " + (stop - start));
    }

    public static void streamCompWrong() {
        // 为什么不能这么写，看上去，因为对于每一个元素，在第一个map生成Completable后，会立即执行join阻塞操作，相当于变成了串行
        long start = System.currentTimeMillis();
        List<Long> prices2 = Stream.of("1", "2", "3", "4", "5")
                .map(p -> CompletableFuture.supplyAsync(() -> CompletableFutureDemo.getPrice("exception1")))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        long stop = System.currentTimeMillis();
        System.out.println("1 collect time elapsed " + (stop - start));
    }

    public static void parallelstream() {
        List<Long> prices = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
                .parallelStream()
                .map(CompletableFutureDemo::getPrice)
                .collect(Collectors.toList());
    }

    public static Long getPrice(String prod) {
        delay();  //模拟服务响应的延迟
        Long price = ThreadLocalRandom.current().nextLong(0, 1000);
        System.out.println("Executing in " + Thread.currentThread().getName() + ", get price for " + prod + " is " + price);
        return price;
    }

    public static Long getExcept(String prod) {
        delay();  //模拟服务响应的延迟
        return 1L / 0;
    }

    private static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
