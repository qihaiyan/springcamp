# spring-native

> English | [中文](README.zh-CN.md)

Spring Boot 3 AOT (GraalVM Native Image) application example.

## Run

Run on the JVM:

```bash
./gradlew :spring-native:bootRun
```

Build a native image (requires a local GraalVM):

```bash
./gradlew :spring-native:nativeCompile
build/native/nativeCompile/spring-native
```

## Blog

[Spring Boot 3的AOT（GraalVM Native Image）应用开发](https://springcamp.cn/spring-native/)（Chinese）
