# spring-taskexecutor-block

Spring 自带线程池（TaskExecutor）配置不当导致线程阻塞/死锁的问题演示：核心线程数与队列容量配满后任务相互等待。启动后观察 taskExecutor 线程池行为与日志。

## 运行

纯源码演示工程（未配置 Gradle 构建），在 IDE 中直接运行 `CfblockApplication` 的 main 方法即可。

## 配套博客

[spring自带线程池使用不当导致的死锁问题](https://springcamp.cn/java-concurrent-thread-block/)
