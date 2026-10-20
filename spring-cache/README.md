# spring-cache

> English | [中文](README.zh-CN.md)

Spring Cache abstraction: cache annotations backed by Redis, with TTL configuration.

## Run

Prerequisite: a local Redis on `localhost:6379`.

```bash
./gradlew :spring-cache:bootRun
```

Tests use an embedded Redis (no install needed): `./gradlew :spring-cache:test`

## Blog

[SpringBoot项目中使用redis缓存](https://springcamp.cn/spring-data-cache/)（Chinese）
