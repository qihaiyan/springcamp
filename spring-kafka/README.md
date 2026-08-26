# spring-kafka

Spring 使用 Kafka 的三种方式：@KafkaListener、MessageListenerContainer、Spring Cloud Stream。

## 运行

示例通过单元测试演示，使用 @EmbeddedKafka 内嵌 Kafka，无需安装 Kafka：

```bash
./gradlew :spring-kafka:test
```

## 配套博客

[spring使用kafka的三种方式（listener、container、stream）](https://springcamp.cn/spring-kafka/)
