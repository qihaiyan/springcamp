import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// dev 阶段把 /api 代理到 Spring 后端（默认 8080），前端同源调用，无需 CORS
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
