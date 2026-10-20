# spring-taskexecutor-block

> English | [中文](README.zh-CN.md)

How a misconfigured Spring TaskExecutor can deadlock: with core threads and queue capacity maxed out, tasks end up waiting on each other. Watch the taskExecutor pool behavior and logs after startup.

## Run

Plain source demo (no Gradle build configured) — run `CfblockApplication#main` directly in your IDE.

## Blog

[spring自带线程池使用不当导致的死锁问题](https://springcamp.cn/java-concurrent-thread-block/)（Chinese）
