# spring-data-jpa-multisource

Spring Data JPA 多数据源示例：主数据源与第二数据源（`other.datasource`）各自独立配置 EntityManager 与事务。

## 运行

`bootRun` 需本地 MySQL（`localhost:3306/demo`，root/root，主数据源为内嵌 H2）；单元测试全部使用 H2，无需安装：

```bash
./gradlew :spring-data-jpa-multisource:test
```

## 配套博客

[SpringBoot项目中的多数据源支持](https://springcamp.cn/spring-jpa-multi-datasource/)
