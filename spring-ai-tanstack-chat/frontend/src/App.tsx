import { useEffect, useRef, useState } from 'react'
import { fetchServerSentEvents, useChat } from '@tanstack/ai-react'
import type { UIMessage } from '@tanstack/ai-react'
import './chat.css'

/**
 * 聊天界面：Spring AI 后端 + TanStack AI 前端。
 *
 * useChat 通过 fetchServerSentEvents('/api/chat') 以 AG-UI 协议与后端通信：
 * 每轮 POST 完整对话历史，后端返回的 TEXT_MESSAGE_CONTENT 事件（逐 token）
 * 会被增量拼进消息 parts，React 自动渲染出逐字打字效果。
 */
export default function App() {
  // 换 threadId 即新会话：useChat 按 threadId 创建独立的 ChatClient，key 触发重挂载
  const [threadId, setThreadId] = useState(() => crypto.randomUUID())

  return (
    <div className="app">
      <Chat key={threadId} threadId={threadId} onNewThread={() => setThreadId(crypto.randomUUID())} />
    </div>
  )
}

function Chat({ threadId, onNewThread }: { threadId: string; onNewThread: () => void }) {
  const [input, setInput] = useState('')
  const { messages, sendMessage, isLoading, stop, error } = useChat({
    connection: fetchServerSentEvents('/api/chat'),
    threadId,
  })

  const listRef = useRef<HTMLDivElement>(null)
  useEffect(() => {
    listRef.current?.scrollTo({ top: listRef.current.scrollHeight })
  }, [messages])

  function submit(event: React.FormEvent) {
    event.preventDefault()
    const text = input.trim()
    if (!text || isLoading) return
    sendMessage(text)
    setInput('')
  }

  // AI 气泡骨架未到时（请求已发出、TEXT_MESSAGE_START 未返回）先显示打字动画
  const waitingFirstChunk =
    isLoading && (messages.length === 0 || messages[messages.length - 1]?.role === 'user')

  return (
    <>
      <header className="app-header">
        <div className="brand">
          <div className="logo">TA</div>
          <div>
            <h1>SpringCamp · TanStack AI Chat</h1>
            <p>Spring AI 后端 + @tanstack/ai-react 前端（AG-UI 协议 / SSE 流式）</p>
          </div>
        </div>
        <button className="btn-ghost" onClick={onNewThread}>
          ＋ 新会话
        </button>
      </header>

      <div className="messages" ref={listRef}>
        {messages.length === 0 && !isLoading && (
          <div className="empty">向 AI 助手提问，回答会逐字流入下方 👇</div>
        )}
        {messages.map((message) => (
          <MessageBubble key={message.id} message={message} />
        ))}
        {waitingFirstChunk && (
          <div className="message assistant">
            <div className="avatar">AI</div>
            <div className="bubble">
              <span className="typing-dots">
                <i />
                <i />
                <i />
              </span>
            </div>
          </div>
        )}
        {error && <div className="error-banner">⚠️ {error.message}</div>}
      </div>

      <form className="composer" onSubmit={submit}>
        <input
          value={input}
          onChange={(event) => setInput(event.target.value)}
          placeholder={isLoading ? 'AI 回答中…' : '输入消息，回车发送'}
        />
        {isLoading ? (
          <button type="button" className="btn-stop" onClick={() => stop()}>
            停止
          </button>
        ) : (
          <button type="submit" className="btn-send" disabled={!input.trim()}>
            发送
          </button>
        )}
      </form>
    </>
  )
}

function MessageBubble({ message }: { message: UIMessage }) {
  const text = message.parts.map((part) => (part.type === 'text' ? part.content : '')).join('')
  const isUser = message.role === 'user'
  // assistant 消息已开始但第一个 token 未到时，气泡内先显示打字动画
  const showTyping = !isUser && !text

  return (
    <div className={`message ${isUser ? 'user' : 'assistant'}`}>
      <div className="avatar">{isUser ? '我' : 'AI'}</div>
      <div className="bubble">
        {text}
        {showTyping && (
          <span className="typing-dots">
            <i />
            <i />
            <i />
          </span>
        )}
      </div>
    </div>
  )
}
