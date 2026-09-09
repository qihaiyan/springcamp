# spring-ai-htmx-chat

Spring AI + HTMX 4 + Alpine.js + Thymeleaf 实现的流式 AI 聊天界面：不写前端框架、不打包 npm，
纯服务端渲染 HTML 片段 + SSE 流式输出，即可获得类 ChatGPT 的逐字打字效果。

技术栈版本：**htmx 4.0.0**（fetch 重写版，SSE 流式交换回归核心）、**Alpine.js 3.17.2**。

## 功能

- **流式输出**：AI 回答通过 SSE（`text/event-stream`）逐 token 流入页面，带打字动画
- **多轮对话**：服务端 `MessageWindowChatMemory` 按会话 id 保存上下文（最近 20 条）
- **新建会话**：一键换新会话 id，同时清空服务端记忆与页面消息
- **无前端构建**：htmx/Alpine.js 走 CDN，样式为单个手写 CSS 文件

## 运行

### 方式一：demo 模式（无需 API Key）

内置一个本地假模型，把一段固定回复逐字吐出，用于体验完整交互流程：

```bash
./gradlew :spring-ai-htmx-chat:bootRun --args='--spring.profiles.active=demo'
```

### 方式二：接入真实大模型（DeepSeek）

1. 修改 `src/main/resources/application.properties` 中的 `spring.ai.deepseek.api-key`
   （或设置环境变量 `DEEPSEEK_API_KEY`）；
2. 启动：

```bash
./gradlew :spring-ai-htmx-chat:bootRun
```

浏览器打开 <http://localhost:8080>。

## 实现要点

htmx 4 基于 fetch 重写，随包发布的 `sse` 扩展可以直接消费**普通请求返回的
`text/event-stream` 响应**——每条 SSE 消息被当作一次局部交换。因此整个聊天只需要一个接口：

```
浏览器                                   服务端
  │ POST /chat (htmx 表单)                │
  │      Accept: text/event-stream        │
  │──────────────────────────────────────▶│ 调用大模型流式接口
  │◀─ SSE 消息 1：对话骨架                 │  用户气泡 + AI 气泡（含打字动画、唯一 id）
  │◀─ SSE 消息 2：<hx-partial …>累积文本   │  每个 token 一条消息
  │◀─ SSE 消息 3：<hx-partial …>累积文本   │
  │◀─ …流结束（连接关闭）                   │
```

- **htmx**：表单 `hx-post` + `hx-swap="beforeend"`，第 1 条 SSE 消息把「用户气泡 + AI 气泡骨架」
  追加进消息列表；后续每条消息是一个 `<hx-partial hx-target="#ans-xxx" hx-swap="innerHTML">`
  片段，整段替换 AI 气泡文本——第一个 token 到达时打字动画自然消失
- **请求生命周期**：htmx 4 默认 `sse.releaseOn:end`，SSE 流结束才触发
  `htmx:finally:request`（注意 v4 事件名是冒号风格），天然防止流式期间重复提交
- **Alpine.js**：监听 `@htmx:before:request`（锁定输入、清空输入框）、`@htmx:finally:request`
  （流结束解锁）、`@htmx:sse:after:message`（每条消息后滚动到底部），管理按钮/空态提示等状态
- **Spring AI**：`ChatClient` + `MessageChatMemoryAdvisor` 提供带记忆的流式对话，
  会话 id 通过 `ChatMemory.CONVERSATION_ID` 参数按请求传入
- **XSS 防护**：用户消息经 `th:text` 输出、模型 token 输出前手动转义

## 关键文件

| 文件 | 说明 |
| --- | --- |
| `ChatController.java` | POST /chat 返回 SSE 流（骨架消息 + 逐 token 的 hx-partial 消息），DELETE 清空会话 |
| `ChatConfig.java` | ChatClient / ChatMemory 装配，demo 模式假模型 |
| `templates/index.html` | 聊天页面，htmx 4 + Alpine.js 交互 |
| `templates/fragments/chat.html` | 一轮对话的开始片段（用户气泡 + AI 气泡骨架） |
| `static/css/chat.css` | 界面样式 |

## 测试

测试中用 `@MockitoBean` 替换 `DeepSeekChatModel`，不依赖真实 API Key：

```bash
./gradlew :spring-ai-htmx-chat:test
```

## 配套博客

—
