<script setup>
import { useRouter } from 'vue-router'
import { projectName } from '../config/config.default.js'

// 路由实例
const router = useRouter()
// 返回登录页
const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="not-found-page">
    <!-- 极简装饰背景（替代原本的大色块） -->
    <span class="blob blob--lg" aria-hidden="true"></span>
    <span class="blob blob--sm" aria-hidden="true"></span>
    <span class="dot-grid" aria-hidden="true"></span>

    <!-- 卡片主体 -->
    <div class="not-found-card">
      <div class="error-code">404</div>
      
      <h1 class="error-title">页面未找到</h1>
      <p class="error-message">抱歉，您访问的页面不存在或已被移除</p>
      
      <el-button
        class="back-button"
        size="large"
        @click="goToLogin"
      >
        返回登录页
      </el-button>
      
      <div class="brand-footer">
        <p>© {{ new Date().getFullYear() }} {{ projectName }}. 保留所有权利</p>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
/* ==================== 设计令牌（与登录页保持一致） ==================== */
.not-found-page {
  --c-primary: #00d639;          /* 主色：亮绿 */
  --c-primary-dark: #00bd33;     /* 主色 hover */
  --c-primary-soft: rgba(0, 214, 57, 0.12); /* 聚焦光晕/装饰 */
  --c-bg: #f5f6fa;               /* 页面背景 */
  --c-surface: #ffffff;          /* 卡片背景 */
  --c-text: #1e1f2b;             /* 主文字 */
  --c-text-sub: #8b90a0;         /* 辅助文字 */
  --c-text-light: #b4b9c6;       /* 极弱文字 */

  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--c-bg);
  position: relative;
  overflow: hidden;
  color: var(--c-text);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI',
    'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* ==================== 装饰元素 ==================== */
.blob {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  
  &--lg {
    width: 400px;
    height: 400px;
    right: -100px;
    bottom: -150px;
    background: radial-gradient(circle at 30% 30%, rgba(0, 214, 57, 0.15), rgba(0, 214, 57, 0) 70%);
  }
  
  &--sm {
    width: 200px;
    height: 200px;
    left: -50px;
    top: -50px;
    background: radial-gradient(circle at 40% 40%, rgba(0, 214, 57, 0.12), rgba(0, 214, 57, 0) 70%);
  }
}

.dot-grid {
  position: absolute;
  right: 15%;
  top: 15%;
  width: 120px;
  height: 88px;
  background-image: radial-gradient(rgba(0, 214, 57, 0.3) 1.6px, transparent 1.6px);
  background-size: 16px 16px;
  opacity: 0.7;
  pointer-events: none;
}

/* ==================== 卡片主体 ==================== */
.not-found-card {
  width: 100%;
  max-width: 480px;
  padding: 56px 48px;
  text-align: center;
  background: var(--c-surface);
  border-radius: 24px;
  /* 极柔和的微阴影 */
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03), 0 24px 60px rgba(17, 24, 39, 0.04);
  position: relative;
  z-index: 1;
  margin: 20px;
}

.error-code {
  font-size: 110px;
  font-weight: 800;
  line-height: 1;
  margin-bottom: 16px;
  letter-spacing: -2px;
  /* 使用主色作为文字渐变 */
  background: linear-gradient(135deg, var(--c-primary) 0%, #00a82d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.error-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--c-text);
  margin: 0 0 12px 0;
  letter-spacing: -0.3px;
}

.error-message {
  font-size: 14px;
  color: var(--c-text-sub);
  margin: 0 0 36px 0;
  line-height: 1.6;
}

/* ==================== 返回按钮（复用登录按钮风格） ==================== */
.back-button {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 1px;
  /* 覆盖 Element Plus 按钮色板变量 */
  --el-button-bg-color: var(--c-primary);
  --el-button-border-color: var(--c-primary);
  --el-button-text-color: #ffffff;
  --el-button-hover-bg-color: var(--c-primary-dark);
  --el-button-hover-border-color: var(--c-primary-dark);
  --el-button-hover-text-color: #ffffff;
  box-shadow: 0 8px 20px rgba(0, 214, 57, 0.22);
  transition: transform 0.22s ease, box-shadow 0.22s ease, background 0.22s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 10px 26px rgba(0, 214, 57, 0.34);
  }
}

/* ==================== 底部版权 ==================== */
.brand-footer {
  margin-top: 40px;
  font-size: 12px;
  color: var(--c-text-light);
  
  p {
    margin: 0;
  }
}

/* ==================== 响应式适配 ==================== */
@media (max-width: 900px) {
  .not-found-card {
    padding: 40px 24px;
  }
  .error-code {
    font-size: 90px;
  }
  .error-title {
    font-size: 22px;
  }
  .dot-grid {
    display: none;
  }
}
</style>