# spring-sse

Server-Sent Events 服务端推送示例：SseEmitter 封装、连接注册表、事件历史与断线重连回放。

## 运行

无外部依赖。启动后访问 `http://localhost:8080` 下的 SSE 接口体验推送与重连。

```bash
./gradlew :spring-sse:bootRun
```
