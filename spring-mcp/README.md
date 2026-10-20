# spring-mcp

> English | [中文](README.zh-CN.md)

Spring AI MCP (Model Context Protocol) example: a weather MCP server built on Spring MVC with SSE transport.

## Run

No external dependencies. Once started, connect an MCP client over SSE to `http://localhost:8080/sse`.

```bash
./gradlew :spring-mcp:bootRun
```
