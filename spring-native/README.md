# spring-native

Spring Boot 3 AOT（GraalVM Native Image）应用开发示例。

## 运行

JVM 方式直接运行：

```bash
./gradlew :spring-native:bootRun
```

构建原生镜像（需本地 GraalVM）：

```bash
./gradlew :spring-native:nativeCompile
build/native/nativeCompile/spring-native
```

## 配套博客

[Spring Boot 3的AOT（GraalVM Native Image）应用开发](https://springcamp.cn/spring-native/)
