<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js";
import { Lock } from "lucide-vue-next";

// 获取当前登录用户信息
const user = ref(JSON.parse(localStorage.getItem("user") || "{}"));

const router = useRouter();

// 表单引用
const passFormRef = ref(null);

// 表单数据
const form = reactive({
  id: user?.value.id,
  username:user?.value.username,
  password: '',
  newPassword: '',
  confirmPassword: '',
  token:user?.value.token,
});

// 表单验证规则
const rules = {
  password: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请输入确认密码', trigger: 'blur' },
    { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
  ]
};

// 保存方法
const save = () => {
  passFormRef.value.validate((valid) => {
    if (valid) {
      if (form.newPassword !== form.confirmPassword) {
        ElMessage.error('两次输入的新密码不相同');
        return false;
      }
      form.value
      request.post('/users/password', form).then(res => {
        if (res.code === 1) {
          ElMessage.success('密码修改成功');
          localStorage.removeItem('user');
          setTimeout(() => {
            router.push('/login');
          }, 1500);
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  });
};
</script>

<template>
  <div class="password-container">
    <section class="password-card">
      <!-- ==================== 卡片头部 ==================== -->
      <header class="card-header">
        <div class="header-icon">
          <Lock :size="20" />
        </div>
        <div class="header-text">
          <h1 class="header-title">修改密码</h1>
          <p class="header-subtitle">定期更换密码，保障你的账号安全</p>
        </div>
      </header>

      <!-- ==================== 表单区 ==================== -->
      <el-form
        ref="passFormRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="password-form"
      >
        <el-form-item label="原密码" prop="password">
          <el-input
            v-model="form.password"
            placeholder="请输入当前密码"
            autocomplete="off"
            show-password
            class="field-input"
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            placeholder="请输入新密码"
            autocomplete="off"
            show-password
            class="field-input"
          />
        </el-form-item>

        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            placeholder="请再次输入新密码"
            autocomplete="off"
            show-password
            class="field-input"
          />
        </el-form-item>

        <!-- ==================== 操作按钮区 ==================== -->
        <div class="form-footer">
          <el-button class="btn-primary" @click="save">确认修改密码</el-button>
          <el-button class="btn-default" @click="passFormRef.resetFields()">重置</el-button>
        </div>
      </el-form>

      <!-- ==================== 安全提示 ==================== -->
      <div class="password-tips">
        <p class="tips-title">密码安全提示</p>
        <ul class="tips-list">
          <li>密码长度至少 3 位字符</li>
          <li>建议使用字母、数字和特殊字符的组合</li>
          <li>定期更换密码可以提高账户安全性</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
/* ==================== 设计令牌 ==================== */
.password-container {
  --c-primary: #00d03c;
  --c-primary-dark: #00bd36;
  --c-primary-soft: rgba(0, 208, 60, 0.12);
  --c-bg: #f4f5f8;
  --c-text: #1a1a24;
  --c-text-sub: #9ca3af;
  --c-border: #eceef4;
  --c-border-soft: #f1f2f6;

  display: flex;
  align-items: flex-start;
  justify-content: center;
  width: 100%;
  min-height: 100vh;

  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
}

.password-card {
  width: 100%;
  max-width: 576px;
  padding: 12px;
  background-color: #fff;
  border-radius: 24px;


  @media (min-width: 640px) {
    padding: 12px;
  }
}

/* ==================== 卡片头部 ==================== */
.card-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 28px;
}

.header-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 44px;
  height: 44px;
  border-radius: 16px;
  background-color: var(--c-primary-soft);
  color: var(--c-primary);
}

.header-text {
  min-width: 0;
}

.header-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--c-text);
}

.header-subtitle {
  margin: 2px 0 0;
  font-size: 14px;
  font-weight: 300;
  color: var(--c-text-sub);
}

/* ==================== 表单 ==================== */
.password-form {
  /* 让每个表单项之间有充足留白 */
  :deep(.el-form-item) {
    margin-bottom: 22px;
  }

  :deep(.el-form-item__label) {
    padding-bottom: 8px;
    font-size: 14px;
    font-weight: 500;
    line-height: 1.4;
    color: var(--c-text);
  }

  /* 校验错误信息稍微轻一点 */
  :deep(.el-form-item__error) {
    padding-top: 4px;
    font-size: 12px;
    color: #f56c6c;
  }
}

/* ==================== 输入框 ==================== */
.field-input {
  :deep(.el-input__wrapper) {
    min-height: 44px;
    padding: 0 16px;
    border: 1px solid transparent;
    border-radius: 16px;
    background-color: var(--c-bg);
    box-shadow: none;
    transition: all 0.2s;
  }

  :deep(.el-input__wrapper:hover) {
    border-color: var(--c-border);
    background-color: #fff;
  }

  :deep(.el-input__wrapper.is-focus) {
    border-color: var(--c-primary);
    background-color: #fff;
    box-shadow: 0 0 0 3px var(--c-primary-soft);
  }

  :deep(.el-input__inner) {
    font-size: 14px;
    font-weight: 500;
    color: var(--c-text);
    -webkit-text-fill-color: var(--c-text);
  }

  :deep(.el-input__inner::placeholder) {
    font-weight: 400;
    color: var(--c-text-sub);
    -webkit-text-fill-color: var(--c-text-sub);
  }
}

/* ==================== 操作按钮区 ==================== */
.form-footer {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 8px;
  justify-content: center;

  @media (min-width: 640px) {
    flex-direction: row;
    align-items: center;
  }
}

.btn-primary,
.btn-default {
  width: 100%;
  height: 44px;
  padding: 0 32px;
  border-radius: 9999px;
  font-weight: 600;
  transition: all 0.2s;

  @media (min-width: 640px) {
    width: auto;
  }
}

.btn-primary {
  background-color: var(--c-primary) !important;
  border: none !important;
  color: #fff !important;
  box-shadow: 0 8px 20px rgb(0 208 60 / 0.25);

  &:hover {
    background-color: var(--c-primary-dark) !important;
  }
}

.btn-default {
  background-color: var(--c-bg) !important;
  border: 1px solid var(--c-border) !important;
  color: #6b7280 !important;
  font-weight: 500;

  &:hover {
    border-color: rgb(0 208 60 / 0.3) !important;
    background-color: rgb(0 208 60 / 0.1) !important;
    color: var(--c-primary) !important;
  }
}

/* ==================== 安全提示 ==================== */
.password-tips {
  margin-top: 28px;
  padding: 18px 20px;
  border-radius: 16px;
  background-color: var(--c-bg);
}

.tips-title {
  margin: 0 0 10px;
  font-size: 13px;
  font-weight: 600;
  color: var(--c-text);
}

.tips-list {
  margin: 0;
  padding-left: 18px;

  li {
    font-size: 13px;
    font-weight: 400;
    line-height: 1.9;
    color: var(--c-text-sub);
  }
}
</style>