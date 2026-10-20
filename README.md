# SpringCamp 🏕️

> English | [中文说明](README.zh-CN.md)

> 30+ **self-contained, runnable Spring Boot examples**, each paired with a hands-on Chinese blog post on [springcamp.cn](https://springcamp.cn).
> Not a pile of docs — working homework you can clone, run and copy from.

[![Apache-2.0](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
![GitHub stars](https://img.shields.io/github/stars/qihaiyan/springcamp)

## What is this

SpringCamp turns those Spring scenarios where "half a day of docs, a pile of blog posts, and you still can't do it" into **minimal runnable examples**: clone it, run it, tweak two lines and it clicks.
It covers Spring AI, Spring Security / OAuth2, Spring Cloud, Data, messaging, caching, concurrency and more.

## Why use it

- **It runs**: every module is a standalone Spring Boot project, not a code snippet.
- **It's documented**: every example pairs with a hands-on article on springcamp.cn — code and principles together.
- **It's current**: tracks new Spring Boot features (HTTP Interface, RestClient, Spring AI, MCP, ...).

## How to run

```bash
git clone https://github.com/qihaiyan/springcamp.git
cd springcamp
./gradlew :<module>:bootRun        # see each module's README for details
```

## Module index

| Module                                                               | What it does                                        | Blog                                                     |
| ------------------------------------------------------------------ | ------------------------------------------------- | -------------------------------------------------------- |
| [spring-ai-deepseek](spring-ai-deepseek)                           | Spring AI + DeepSeek (chat / streaming / reasoning / function calling) | [post](https://springcamp.cn/spring-ai-deepseek/)        |
| [spring-ai-htmx-chat](spring-ai-htmx-chat)                         | Streaming AI chat UI with HTMX 4 + Alpine.js      | [post](https://springcamp.cn/spring-ai-htmx-chat/)       |
| [spring-mcp](spring-mcp)                                           | Spring MCP (Model Context Protocol) server example | —                                                        |
| [spring-advanced-security](spring-advanced-security)               | Advanced Spring Security (custom login / auth)     | [post](https://springcamp.cn/spring-advanced-security/)  |
| [spring-cloud-gateway](spring-cloud-gateway)                       | Spring Cloud Gateway dynamic routing               | [post](https://springcamp.cn/spring-cloud-gateway/)      |
| [spring-kafka](spring-kafka)                                       | Spring Kafka messaging (3 usage styles)            | [post](https://springcamp.cn/spring-kafka/)              |
| [spring-cache](spring-cache)                                       | Spring Cache abstraction                           | [post](https://springcamp.cn/spring-data-cache/)         |
| [spring-aop](spring-aop)                                           | Spring AOP aspects                                 | [post](https://springcamp.cn/spring-boot-aop/)           |
| [spring-data-flex-query](spring-data-flex-query)                   | Flexible Spring Data queries (SQL-injection safe)  | [post](https://springcamp.cn/spring-data-flex-query/)    |
| [spring-data-jpa-multisource](spring-data-jpa-multisource)         | Spring Data JPA multiple datasources               | [post](https://springcamp.cn/spring-jpa-multi-datasource/) |
| [spring-dynamic-datasource](spring-dynamic-datasource)             | Dynamic datasource switching                       | [post](https://springcamp.cn/spring-dynamic-datasource/) |
| [spring-dynamic-scheduler](spring-dynamic-scheduler)               | Dynamic scheduled-task control                     | [post](https://springcamp.cn/spring-dynamic-scheduler/)  |
| [spring-http-interface](spring-http-interface)                     | Spring Boot 3.2 declarative HTTP Interface         | [post](https://springcamp.cn/spring-boot-http-interface/) |
| [spring-rest-client](spring-rest-client)                           | Spring Boot 3.2 RestClient                         | [post](https://springcamp.cn/spring-boot-rest-client/)   |
| [spring-rest-template-log](spring-rest-template-log)               | RestTemplate calls + DNS timeout config            | [post](https://springcamp.cn/spring-resttemplate-dns-timeout/) |
| [spring-rest-log-request-response](spring-rest-log-request-response) | REST request/response logging                    | [post](https://springcamp.cn/spring-rest-log-request-response/) |
| [spring-modify-request-body](spring-modify-request-body)           | Modify RequestBody in one place                    | [post](https://springcamp.cn/spring-controller-modify-request-body/) |
| [spring-sse](spring-sse)                                           | Server-Sent Events push                            | —                                                        |
| [spring-native](spring-native)                                     | GraalVM native image                               | [post](https://springcamp.cn/spring-native/)             |
| [spring-data-envers-conditional](spring-data-envers-conditional)   | Conditional audit logging (Envers)                 | [post](https://springcamp.cn/spring-data-conditional-auditing/) |
| [spring-data-jdbc-client](spring-data-jdbc-client)                 | Spring Data JDBC Client (JdbcClient)               | [post](https://springcamp.cn/spring-boot-jdbc-client/)   |
| [spring-easy-rule](spring-easy-rule)                               | Easy Rules rule engine                             | [post](https://springcamp.cn/easy-rules/)                |
| [spring-groovy](spring-groovy)                                     | Groovy dynamic scripts                             | [post](https://springcamp.cn/spring-groovy/)             |
| [spring-redis-resolver](spring-redis-resolver)                     | Redis-backed cache resolver                        | [post](https://springcamp.cn/spring-redis-resolver/)     |
| [spring-localdatetime-epoch](spring-localdatetime-epoch)           | LocalDateTime ↔ epoch conversion                   | [post](https://springcamp.cn/spring-localdatetime-epoch/) |
| [spring-taskexecutor-block](spring-taskexecutor-block)             | TaskExecutor deadlock demo                         | [post](https://springcamp.cn/java-concurrent-thread-block/) |
| [spring-unit-test](spring-unit-test)                               | Spring unit testing                                | [post](https://springcamp.cn/spring-boot-unit-test/)     |
| [elasticsearch-javaclient](elasticsearch-javaclient)               | Elasticsearch Java client                          | [post](https://springcamp.cn/elasticsearch-javaclient/)  |
| [java-concurrency](java-concurrency)                               | Java concurrency                                   | [post](https://springcamp.cn/java-concurrency/)          |
| [utils](utils)                                                     | Common utilities                                   | —                                                        |

> Blog posts are written in Chinese. Full list at [springcamp.cn](https://springcamp.cn).


![Alt](https://repobeats.axiom.co/api/embed/f7755d6c54108961b98e66b3e26aa12467444763.svg "Repobeats analytics image")

### Supported by

[JetBrains](https://jb.gg/OpenSourceSupport)

![JetBrains Logo (Main) logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)
