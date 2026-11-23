import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173,
    proxy: {
      // 前端请求 /api/xxx → 转发到 Spring Boot
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        // 如果后端接口路径本身不带 /api 前缀，就开启下面这行去掉 /api
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})