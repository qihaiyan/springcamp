# spring-mcp

Spring AI MCP（Model Context Protocol）集成示例：基于 webmvc 实现一个天气查询 MCP Server（SSE 传输）。

## 运行

无外部依赖。启动后即可用 MCP 客户端通过 SSE 连接 `http://localhost:8080/sse`。

```bash
./gradlew :spring-mcp:bootRun
```
