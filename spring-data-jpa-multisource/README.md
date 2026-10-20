# spring-data-jpa-multisource

> English | [中文](README.zh-CN.md)

Spring Data JPA with multiple datasources: the primary datasource and the second one (`other.datasource`) each get their own EntityManager and transaction manager.

## Run

`bootRun` expects a local MySQL (`localhost:3306/demo`, root/root; the primary datasource is embedded H2). Unit tests run entirely on H2 — no installation required:

```bash
./gradlew :spring-data-jpa-multisource:test
```

## Blog

[SpringBoot项目中的多数据源支持](https://springcamp.cn/spring-jpa-multi-datasource/)（Chinese）
