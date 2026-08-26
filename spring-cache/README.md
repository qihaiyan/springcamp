# spring-cache

Spring Cache 缓存抽象示例：基于 Redis 的缓存注解使用与 TTL 配置。

## 运行

前置条件：本地 Redis（`localhost:6379`）。

```bash
./gradlew :spring-cache:bootRun
```

测试使用内嵌 Redis，无需安装：`./gradlew :spring-cache:test`

## 配套博客

[SpringBoot项目中使用redis缓存](https://springcamp.cn/spring-data-cache/)
