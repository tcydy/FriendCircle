<script setup>

import request from "@/utils/request.js";
import {useRouter} from "vue-router";
import {reactive,ref} from "vue";
import {User, Lock, Key, DataAnalysis} from '@element-plus/icons-vue'
import {ElMessage} from "element-plus";
import { projectName } from "../config/config.default.js";

//路由实例
const router=useRouter()
const userFromInst=ref(null)

//注册表单
const registerForm=reactive({
  username: '',
  nickname:'',
  password:'',
  confirmPassword:''
})

//是否同意
const isAllow=ref(true)

//表单验证规则
const rules={
  username:[
    {required:true,message:'',trigger:'blur'},
    {min:3,max:10,message: '长度在3到10个字符',trigger: 'blur'}
  ],
  password:[
    {required:true,message:'',trigger:'blur'},
    {min:1,max:20,message:'长度在1到20个字符',trigger:'blur'}
  ],
  confirmPassword:[
    {required:true,message:'',trigger:'blur'},
    {min:1,max:20,message:'长度在1到20个字符',trigger:'blur'}
  ]
}

//注册方法
const register=()=>
{
  //表单校验合法
  userFromInst.value.validate((valid)=>{
    //若合法
    if(valid)
    {
      //检查两次密码是否一致
      if(registerForm.password!==registerForm.confirmPassword)
      {
        ElMessage.error('两次输入的密码不相同')
        return false
      }

      //创建一个不包含确认密码的对象
      const registerDate={
        username: registerForm.username,
        password:registerForm.password,
      }
      request.post('/web/register',registerDate).then((res)=>{
        if(res.code===1)
        {
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        }
        else{
          ElMessage.error(res.msg || '注册失败')
          console.log(res)
        }
      }).catch(error=>{
        console.error('注册请求错误',error)
        ElMessage.error('注册失败，请稍后再试')
      })
    }
  })
}

</script>


<template>
  <div class="register-page">
    <div class="register-card">
      <!-- ==================== 左侧：品牌 / 宣传区 ==================== -->
      <section class="brand-panel">
        <!-- 装饰性几何图形（纯 CSS，无需图片资源） -->
        <span class="blob blob--lg" aria-hidden="true"></span>
        <span class="blob blob--sm" aria-hidden="true"></span>
        <span class="dot-grid" aria-hidden="true"></span>

        <div class="brand-top">
          <div class="brand-logo">
            <!-- 学士帽 Icon -->
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M22 10 12 5 2 10l10 5 10-5Z" />
              <path d="M6 12v5c0 1.5 3 3 6 3s6-1.5 6-3v-5" />
            </svg>
          </div>
          <span class="brand-name">{{ projectName }}</span>
        </div>

        <div class="brand-body">
          <h1 class="brand-title">选课，<br />从此轻松一点</h1>
          <p class="brand-desc">
            一站式完成课程查询、选课与退课，实时掌握您的课表安排。
          </p>

          <ul class="brand-points">
            <li><span class="point-dot"></span>海量课程实时更新</li>
            <li><span class="point-dot"></span>选课冲突智能提醒</li>
            <li><span class="point-dot"></span>课表一键导出</li>
          </ul>
        </div>

        <p class="brand-foot">© 2026 教务处 · 智慧校园</p>
      </section>

      <!-- ==================== 右侧：注册表单区 ==================== -->
      <section class="form-panel">
        <div class="form-inner">
          <header class="form-header">
            <h2 class="form-title">欢迎注册</h2>
            <p class="form-subtitle"></p>
          </header>

          <el-form
            ref="userFromInst"
            :model="registerForm"
            :rules="rules"
            class="register-form"
            @keydown.enter.prevent="register"
          >
            <!-- 学号 -->
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                size="large"
                placeholder="请输入用户名"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <!-- 密码 -->
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                size="large"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <!-- 确认密码 -->
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                size="large"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <br/>

            <!-- 注册按钮 -->
            <el-form-item class="form-button-item">
              <el-button
                class="register-button"
                size="large"
                :disabled="!isAllow"
                @click="register"
              >
                注 册
              </el-button>
            </el-form-item>
          </el-form>

          <p class="register-link">
            已经有账号？<a class="link" @click="router.push('/login')">立即登录</a>
          </p>
        </div>
      </section>
    </div>
  </div>
</template>



<style scoped>
/* ==================== 设计令牌（Design Tokens） ==================== */
.register-page {
  --c-primary: #00d639;          /* 主色：亮绿 */
  --c-primary-dark: #00bd33;     /* 主色 hover */
  --c-primary-soft: rgba(0, 214, 57, 0.12); /* 聚焦光晕 */
  --c-bg: #f5f6fa;               /* 页面背景 */
  --c-surface: #ffffff;          /* 卡片背景 */
  --c-text: #1e1f2b;             /* 主文字 */
  --c-text-sub: #8b90a0;         /* 辅助文字 */
  --c-text-light: #b4b9c6;       /* 极弱文字 */
  --c-border: #eceef4;
  --c-field: #f7f8fa;            /* 输入框底色 */

  --radius-card: 24px;
  --radius-field: 12px;
  --shadow-card: 0 10px 30px rgba(0, 0, 0, 0.03), 0 24px 60px rgba(17, 24, 39, 0.04);

  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  background: var(--c-bg);
  color: var(--c-text);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI',
    'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* ==================== 卡片容器（左右分栏） ==================== */
.register-card {
  width: 100%;
  max-width: 980px;
  min-height: 600px;
  display: grid;
  grid-template-columns: 1.05fr 1fr;
  background: var(--c-surface);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}

/* ==================== 左侧品牌区 ==================== */
.brand-panel {
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding: 44px 48px;
  background: linear-gradient(155deg, #effff3 0%, #f8fffa 45%, #ffffff 100%);
}

/* 装饰：柔光圆形 */
.blob {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}
.blob--lg {
  width: 340px;
  height: 340px;
  right: -150px;
  bottom: -140px;
  background: radial-gradient(circle at 30% 30%, rgba(0, 214, 57, 0.22), rgba(0, 214, 57, 0) 70%);
}
.blob--sm {
  width: 180px;
  height: 180px;
  right: 40px;
  top: -80px;
  background: radial-gradient(circle at 40% 40%, rgba(0, 214, 57, 0.16), rgba(0, 214, 57, 0) 70%);
}

/* 装饰：点阵纹理 */
.dot-grid {
  position: absolute;
  right: 44px;
  bottom: 120px;
  width: 120px;
  height: 88px;
  background-image: radial-gradient(rgba(0, 214, 57, 0.35) 1.6px, transparent 1.6px);
  background-size: 16px 16px;
  opacity: 0.7;
  pointer-events: none;
}

.brand-top {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}
.brand-logo {
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  color: #fff;
  background: var(--c-primary);
  box-shadow: 0 6px 16px rgba(0, 214, 57, 0.28);
}
.brand-name {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.brand-body {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.brand-title {
  margin: 0 0 16px;
  font-size: 36px;
  line-height: 1.28;
  font-weight: 800;
  letter-spacing: -0.5px;
}
.brand-desc {
  margin: 0 0 28px;
  max-width: 320px;
  font-size: 14px;
  line-height: 1.75;
  color: var(--c-text-sub);
}
.brand-points {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 12px;
}
.brand-points li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13.5px;
  color: #4a4f5e;
}
.point-dot {
  position: relative;
  flex: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--c-primary-soft);
}
.point-dot::after {
  content: '';
  position: absolute;
  inset: 0;
  margin: auto;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--c-primary);
}

.brand-foot {
  position: relative;
  z-index: 1;
  margin: 0;
  font-size: 12px;
  color: var(--c-text-light);
}

/* ==================== 右侧表单区 ==================== */
.form-panel {
  display: flex;
  flex-direction: column;
  padding: 56px 56px 32px;
}
.form-inner {
  width: 100%;
  max-width: 360px;
  margin: auto;
}

.form-title {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.3px;
}
.form-subtitle {
  margin: 0 0 32px;
  font-size: 13.5px;
  color: var(--c-text-sub);
}

/* ---------- Element Plus 输入框定制 ---------- */
.register-form :deep(.el-form-item) {
  margin-bottom: 18px;
}
.register-form :deep(.el-input__wrapper) {
  min-height: 48px;
  padding: 4px 14px;
  border-radius: var(--radius-field);
  border: 1.5px solid transparent;
  background: var(--c-field);
  box-shadow: none;
  transition: background 0.2s, border-color 0.2s, box-shadow 0.2s;
}
.register-form :deep(.el-input__wrapper:hover) {
  border-color: #e2e5ee;
}
/* 聚焦：绿色描边 + 柔和光晕 */
.register-form :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  border-color: var(--c-primary);
  box-shadow: 0 0 0 4px var(--c-primary-soft);
}
.register-form :deep(.el-input__inner) {
  font-size: 14px;
  color: var(--c-text);
}
.register-form :deep(.el-input__inner::placeholder) {
  color: #b0b5c2;
}
.register-form :deep(.el-input__prefix) {
  color: #b0b5c2;
}
.register-form :deep(.el-input__wrapper.is-focus .el-input__prefix) {
  color: var(--c-primary);
}
/* 校验错误态 */
.register-form :deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: #ff5c5c;
  box-shadow: 0 0 0 4px rgba(255, 92, 92, 0.1);
}
.register-form :deep(.el-form-item__error) {
  padding-top: 4px;
  font-size: 12px;
}

/* ---------- 记住密码 / 忘记密码 ---------- */
.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 4px 0 26px;
  font-size: 13px;
}
.remember {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #5b6070;
  cursor: pointer;
  user-select: none;
}
.remember-input {
  width: 15px;
  height: 15px;
  accent-color: var(--c-primary);
  cursor: pointer;
}
.forgot-link {
  color: var(--c-text-sub);
  cursor: pointer;
  transition: color 0.2s;
}
.forgot-link:hover {
  color: var(--c-primary);
}

/* ---------- 登录按钮 ---------- */
.register-button {
  width: 100%;
  height: 48px;
  border: none;
  border-radius: var(--radius-field);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 2px;
  /* 覆盖 Element Plus 按钮色板变量 */
  --el-button-bg-color: var(--c-primary);
  --el-button-border-color: var(--c-primary);
  --el-button-text-color: #ffffff;
  --el-button-hover-bg-color: var(--c-primary-dark);
  --el-button-hover-border-color: var(--c-primary-dark);
  --el-button-hover-text-color: #ffffff;
  --el-button-active-bg-color: var(--c-primary-dark);
  --el-button-active-border-color: var(--c-primary-dark);
  --el-button-disabled-bg-color: #d6f7de;
  --el-button-disabled-border-color: #d6f7de;
  --el-button-disabled-text-color: #ffffff;
  box-shadow: 0 8px 20px rgba(0, 214, 57, 0.22);
  transition: transform 0.22s ease, box-shadow 0.22s ease, background 0.22s ease;
}
.register-button:hover:not(.is-disabled) {
  transform: translateY(-1px);
  box-shadow: 0 10px 26px rgba(0, 214, 57, 0.34);
}
.register-button.is-disabled {
  box-shadow: none;
  cursor: not-allowed;
}

/* ---------- 注册链接 ---------- */
.register-link {
  margin: 18px 0 0;
  text-align: center;
  font-size: 13px;
  color: var(--c-text-sub);
}
.link {
  color: var(--c-primary);
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s;
}
.link:hover {
  color: var(--c-primary-dark);
}

/* ---------- 底部信息 ---------- */
.form-footer {
  margin-top: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 12px;
  color: var(--c-text-light);
}
.form-footer .divider {
  opacity: 0.6;
}
.footer-link {
  color: var(--c-text-light);
  cursor: pointer;
  transition: color 0.2s;
}
.footer-link:hover {
  color: var(--c-primary);
}

/* ==================== 响应式：移动端上下堆叠 ==================== */
@media (max-width: 900px) {
  .register-page {
    align-items: flex-start;
    padding: 20px 16px;
  }
  .register-card {
    grid-template-columns: 1fr;
    max-width: 460px;
    min-height: 0;
    margin: auto 0;
  }
  .brand-panel {
    gap: 20px;
    padding: 28px 28px 24px;
  }
  .brand-body {
    margin-top: 8px;
  }
  .brand-title {
    font-size: 26px;
  }
  .brand-desc,
  .brand-points,
  .brand-foot,
  .dot-grid {
    display: none;
  }
  .form-panel {
    padding: 28px 28px 24px;
  }
  .form-inner {
    max-width: none;
    margin: 0;
  }
  .form-title {
    font-size: 22px;
  }
  .form-subtitle {
    margin-bottom: 24px;
  }
  .form-footer {
    margin-top: 24px;
  }
}
</style>