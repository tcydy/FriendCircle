<script setup>
import { reactive, ref,onMounted } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import { serverHost } from "../../config/config.default.js";
import { Plus, User, ArrowLeft, X, Check } from "lucide-vue-next";

/* ==================== 表单数据 ==================== */
const form = ref({});

/* ==================== 用户信息（本地存储） ==================== */
const user = ref(
  localStorage.getItem("user")
    ? JSON.parse(localStorage.getItem("user"))
    : {}
);



/* ==================== 上传请求头 ==================== */
const uploadHeader = reactive({
  token: user.value.token || "",
});



/* ==================== 事件声明 ==================== */
const emit = defineEmits(["update-user", "refreshUser"]);

/* ==================== 保存修改 ==================== */
const save = () => {
  const upload = ref({});
  upload.value.id = form.value.id;
  upload.value.avatar = form.value.avatar;
  upload.value.nickname = form.value.nickname;
  upload.value.profile = form.value.profile;
  
  // 传参时必须传 .value 拿到真实对象
  request.put('/users/profile', upload.value).then((res) => {
    if (res.code === 1) {
      ElMessage.success("保存成功");
      if (form.value.avatar) {
        user.value.avatar = form.value.avatar;
        user.value.profile = form.value.profile;
        user.value.nickname = form.value.nickname;
        localStorage.setItem("user", JSON.stringify(user.value));
      }
      emit("update-user", user.value);
      emit("refreshUser", user.value);
    } else {
      ElMessage.error(res.msg || "保存失败");
    }
  });

};

//获取用户信息
const loadAccount = () => {
  form.value = localStorage.getItem("user") ?
    JSON.parse(localStorage.getItem("user")) :
    {};
}


/* ==================== 取消修改 ==================== */
const cancel = () => {
  loadAccount();
  ElMessage.info("已取消修改");
};


/* ==================== 头像上传成功 ==================== */
function handleAvatarSuccess(res) {
  console.log("上传返回:", res);
  form.value.avatar = res.data;
  ElMessage.success('头像上传成功，请点击"保存"以保存');
}

/* ==================== 头像上传失败 ==================== */
const handleAvatarError = (error) => {
  console.error("上传失败:", error);
  ElMessage.error("头像上传失败，请重试");
};

/* ==================== 上传前校验 ==================== */
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith("image/");
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("只能上传图片文件！");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过 2MB！");
    return false;
  }
  return true;
};

onMounted(() => {
  loadAccount();
})

</script>

<template>
  <div class="person-container">
    <section class="person-card">
      <!-- ==================== 卡片头部 ==================== -->
      <header class="card-header">
        <div class="header-icon">
          <User :size="20" />
        </div>
        <div class="header-text">
          <h1 class="header-title">个人信息</h1>
          <p class="header-subtitle">管理你的基础信息</p>
        </div>
      </header>

      <!-- ==================== 头像上传区 ==================== -->
      <div class="avatar-container">
        <el-upload class="avatar-uploader" :action="serverHost + '/web/upload'" :headers="uploadHeader"
          :show-file-list="false" :before-upload="beforeAvatarUpload" :on-success="handleAvatarSuccess"
          :on-error="handleAvatarError" accept="image/*">
          <img v-if="form.avatar" :src="form.avatar" alt="用户头像" class="avatar-image" />
          <div v-else class="avatar-placeholder">
            <Plus :size="28" />
          </div>
        </el-upload>
        <p class="avatar-tip">点击上传头像（支持 jpg/png，最大 2MB）</p>
      </div>

      <!-- ==================== 用户名（只读） ==================== -->
      <div class="form-field">
        <label class="field-label">用户名</label>
        <el-input v-model="form.username" disabled autocomplete="off" class="username-input" />
      </div>
      <div class="form-field">
        <label class="field-label">昵称</label>
        <el-input v-model="form.nickname" class="username-input" />
      </div>
      <div class="form-field">
        <label class="field-label">简介</label>
        <el-input v-model="form.profile" class="username-input" />
      </div>

      <!-- ==================== 操作按钮区 ==================== -->
      <div class="form-footer">
        <el-button class="btn-primary" @click="save">保存</el-button>
        <el-button class="btn-default" @click="cancel">取消</el-button>
      </div>
    </section>
  </div>
</template>

<style lang="scss" scoped>
/* ==================== 设计令牌 ==================== */
.person-container {
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

  @media (min-width: 640px) {}
}

.person-card {
  width: 100%;
  max-width: 576px;
  padding: 10px;
  background-color: #fff;

  @media (min-width: 640px) {
    padding: 10px;
  }
}

/* ==================== 卡片头部 ==================== */
.card-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 32px;
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

/* ==================== 头像上传区 ==================== */
.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 36px;
}

.avatar-uploader {
  display: block;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  cursor: pointer;
  overflow: hidden;

  :deep(.el-upload) {
    display: block;
    width: 120px;
    height: 120px;
    border-radius: 50%;
    cursor: pointer;
    overflow: hidden;
  }
}

.avatar-image {
  display: block;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 2px solid var(--c-border);
  object-fit: cover;
  object-position: center;
  transition: all 0.3s;
}

.avatar-uploader:hover .avatar-image {
  transform: scale(1.02);
  border-color: var(--c-primary);
  box-shadow: 0 4px 12px rgb(0 208 60 / 0.2);
}

.avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  border: 2px dashed #dcdce6;
  border-radius: 50%;
  background-color: var(--c-bg);
  color: var(--c-text-sub);
  transition: all 0.3s;
}

.avatar-uploader:hover .avatar-placeholder {
  border-color: var(--c-primary);
  background-color: #fff;
  color: var(--c-primary);
}

.avatar-tip {
  margin: 12px 0 0;
  font-size: 13px;
  font-weight: 300;
  color: var(--c-text-sub);
}

/* ==================== 用户名 ==================== */
.form-field {
  margin-bottom: 40px;
}

.field-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--c-text);
}

.username-input {
  :deep(.el-input__wrapper) {
    min-height: 44px;
    padding: 0 16px;
    border: 1px solid transparent;
    border-radius: 16px;
    background-color: var(--c-bg);
    box-shadow: none;
  }

  :deep(.el-input__inner) {
    font-weight: 500;
    color: var(--c-text);
    -webkit-text-fill-color: var(--c-text);
  }

  /* 禁用状态 */
  :deep(.el-input.is-disabled .el-input__wrapper) {
    background-color: var(--c-bg);
    border-color: transparent;
    box-shadow: none;
  }

  :deep(.el-input.is-disabled .el-input__inner) {
    color: var(--c-text);
    -webkit-text-fill-color: var(--c-text);
  }
}

/* ==================== 操作按钮区 ==================== */
.form-footer {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 24px;
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
</style>