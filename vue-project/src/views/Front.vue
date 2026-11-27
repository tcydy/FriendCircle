<script setup>
import { useRoute, useRouter } from "vue-router";
import { computed, ref } from "vue";
import { ElMessage } from "element-plus";
import { projectName } from "../config/config.default.js";
import {
  Lock,
  SwitchButton,
  House,
  UserFilled,
  OfficeBuilding,
  User,
  Plus,
  Fold,
  Expand
} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();

// 安全读取用户信息
function getUser() {
  try {
    const str = localStorage.getItem("user");
    return str ? JSON.parse(str) : {};
  } catch {
    return {};
  }
}
const user = ref(getUser());

// 侧边栏状态（持久化）
const isCollapse = ref(localStorage.getItem("isCollapse") === "1");
const sideWidth = computed(() => (isCollapse.value ? 64 : 200));
const logoTextShow = computed(() => !isCollapse.value);

// 当前激活菜单
const activeMenu = computed(() => route.path);

// 折叠持久化
const toggleCollapse = (val) => {
  isCollapse.value = val;
  localStorage.setItem("isCollapse", val ? "1" : "0");
};

// 退出
const logout = () => {
  localStorage.removeItem("user");
  ElMessage.success("退出成功");
  router.replace("/login");
};

// 更新用户信息
const handleUpdateUser = (updatedUser) => {
  user.value = updatedUser;
  localStorage.setItem("user", JSON.stringify(updatedUser));
  ElMessage.success("更新成功");
};
const handleUpdateUserFail = () => {
  ElMessage.error("更新失败");
};

// 默认头像
const defaultAvatar = "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";
</script>

<template>
  <div class="admin-layout">
    <!-- 顶部 -->
    <header class="admin-header">
      <div class="header-left">
        <!-- 侧边栏折叠切换按钮 -->
        <div class="collapse-btn" @click="toggleCollapse(!isCollapse)">
          <el-icon :size="35">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>

        <div class="logo-container">
          <router-link to="/front/square" class="dropdown-link">
            <h1 class="logo-text" v-if="logoTextShow">
              {{ projectName }}
            </h1>
          </router-link>
        </div>
      </div>
      <div class="header-right">
        <el-dropdown>
          <div class="user-info">
            <div class="user-avatar">
              <img :src="user.avatar || defaultAvatar" alt="avatar" />
            </div>
            <span class="user-name">
              {{ user.nickname || user.username || "用户" }}
            </span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>
                <router-link to="/front/person" class="dropdown-link">
                  <el-icon>
                    <User />
                  </el-icon>
                  <span>个人信息</span>
                </router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <router-link to="/front/password" class="dropdown-link">
                  <el-icon>
                    <Lock />
                  </el-icon>
                  <span>修改密码</span>
                </router-link>
              </el-dropdown-item>
              <el-dropdown-item>
                <div @click="logout" class="dropdown-link">
                  <el-icon>
                    <SwitchButton />
                  </el-icon>
                  <span>退出登录</span>
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <!-- 内容 -->
    <div class="admin-container">
      <aside class="admin-sidebar" :style="{ width: sideWidth + 'px' }">
        <el-menu :default-active="activeMenu" :collapse="isCollapse" router :collapse-transition="false">
          <el-menu-item index="/front/square">
            <el-icon>
              <House />
            </el-icon>
            <template #title>首页</template>
          </el-menu-item>

          <el-menu-item index="/front/following">
            <el-icon>
              <UserFilled />
            </el-icon>
            <template #title>关注</template>
          </el-menu-item>

          <el-menu-item index="/front/nearby">
            <el-icon>
              <OfficeBuilding />
            </el-icon>
            <template #title>附近</template>
          </el-menu-item>

          <el-menu-item index="/front/profile">
            <el-icon>
              <User />
            </el-icon>
            <template #title>我的</template>
          </el-menu-item>

          <el-menu-item index="/front/publish">
            <el-icon>
              <Plus />
            </el-icon>
            <template #title>发布</template>
          </el-menu-item>

        </el-menu>
      </aside>

      <main class="admin-content">
        <router-view @update-user="handleUpdateUser" @update-user-fail="handleUpdateUserFail" />
      </main>
    </div>

    <!-- 原本的主题抽屉 <el-drawer> 已彻底删除 -->
  </div>
</template>

<style lang="scss" scoped>
/* ==================== 设计令牌（与登录页/404页保持一致） ==================== */
.admin-layout {
  /* 覆盖原有主题变量，默认指向亮绿色体系 */
  --font-color-primary: #00d639;
  --front-color-primary: rgba(0, 214, 57, 0.12);

  /* 新增统一的 UI 令牌 */
  --c-bg: #f5f6fa;
  /* 页面背景 */
  --c-surface: #ffffff;
  /* 卡片背景 */
  --c-text: #1e1f2b;
  /* 主文字 */
  --c-text-sub: #8b90a0;
  /* 辅助文字 */
  --c-border: #eceef4;
  /* 极细边框 */
  --radius-card: 20px;
  /* 大圆角 */
  --shadow-card: 0 10px 30px rgba(0, 0, 0, 0.03), 0 24px 60px rgba(17, 24, 39, 0.04);

  display: flex;
  flex-direction: column;
  min-height: 100vh;
  frontground-color: var(--c-bg);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* ==================== 顶部导航栏（转为白底轻量设计） ==================== */
.admin-header {
  height: 60px;
  frontground-color: var(--c-surface);
  /* 改为白底 */
  color: var(--c-text);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  /* 替换为极细边框或极柔和阴影 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.02);
  border-bottom: 1px solid var(--c-border);
  z-index: 1000;

  .header-left {
    display: flex;
    align-items: center;
    padding: 0 10px;

    .logo-container {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      cursor: pointer;

      .logo-image {
        width: 30px;
        height: 30px;
        margin-right: 10px;
        flex-shrink: 0;
      }

      .logo-text {
        padding: 10px;
        font-size: 18px;
        font-weight: 700;
        /* 标题加粗 */
        color: var(--c-text);
        /* 改为深色字 */
        margin: 0;
        white-space: nowrap;
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    margin-right: 20px;

    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 6px 12px;
      border-radius: 12px;
      /* 增加圆角 */
      transition: frontground 0.2s;

      &:hover {
        frontground-color: var(--c-bg);
        /* 悬停变为浅灰白 */
      }

      .user-avatar {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        overflow: hidden;
        margin-right: 8px;
        frontground-color: var(--c-bg);
        /* 占位底色改为浅灰 */
        border: 1px solid var(--c-border);

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          outline: none !important;
        }
      }

      .user-name {
        font-size: 14px;
        color: var(--c-text);
        /* 改为深色字 */
        font-weight: 500;
      }
    }
  }
}

/* ==================== 主体容器 ==================== */
.admin-container {
  display: flex;
  flex: 1;
  gap: 20px;
  /* 增加卡片之间的间隙 */
  padding: 20px;
  /* 增加外围留白 */
  frontground-color: var(--c-bg);
}

/* ==================== 侧边栏（变身白色大圆角卡片） ==================== */
.admin-sidebar {
  min-height: calc(100vh - 100px);
  /* 修正高度 */
  frontground-color: var(--c-surface);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  transition: width 0.3s;
  overflow: hidden;
  padding: 12px;
  /* 内部留出呼吸感 */
}

/* ==================== 内容区 ==================== */
.admin-content {
  flex: 1;
  overflow: auto;
  frontground-color: var(--c-surface);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  padding: 24px;
}

/* ==================== 下拉菜单链接 ==================== */
.dropdown-link {
  display: flex;
  align-items: center;
  color: var(--c-text);
  text-decoration: none;
  padding: 4px 0;

  .el-icon {
    margin-right: 8px;
    font-size: 16px;
    color: var(--c-text-sub);
  }

  &:hover {
    color: var(--font-color-primary);

    .el-icon {
      color: var(--font-color-primary);
    }
  }
}

/* ==================== 抽屉设置 ==================== */
.drawer-content {
  padding: 10px;

  .drawer-section {
    margin-bottom: 24px;

    h3 {
      margin-top: 0;
      margin-bottom: 20px;
      font-size: 15px;
      font-weight: 600;
      color: var(--c-text);
    }

    .drawer-option {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      span {
        font-size: 14px;
        color: var(--c-text-sub);
      }
    }
  }

  .theme-options {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    .theme-option {
      position: relative;
      width: 50px;
      height: 50px;
      border-radius: 12px;
      /* 圆角微调 */
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 2px solid transparent;
      frontground-color: var(--c-bg);
      transition: all 0.2s;

      &.active {
        border-color: var(--font-color-primary);
      }

      .theme-color {
        width: 32px;
        height: 32px;
        border-radius: 8px;
        frontground-color: var(--font-color-primary);
      }

      .theme-check {
        position: absolute;
        bottom: -4px;
        right: -4px;
        width: 18px;
        height: 18px;
        frontground-color: #fff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        color: var(--font-color-primary);
        font-weight: bold;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
      }
    }
  }
}
</style>

<style scoped>
/* ==================== 覆盖 Element Plus 菜单样式，使其扁平、圆润、亮绿 ==================== */
.el-menu {
  frontground-color: transparent !important;
  border: none !important;
}

/* 菜单项基础样式 */
.el-menu-item,
:deep(.el-sub-menu__title) {
  height: 48px !important;
  line-height: 48px !important;
  border-radius: 10px !important;
  /* 圆角按钮感 */
  margin-bottom: 6px;
  color: var(--c-text-sub) !important;
  transition: all 0.2s ease;
}

/* 悬停态 */
.el-menu-item:hover,
:deep(.el-sub-menu__title:hover) {
  color: var(--c-text) !important;
  frontground-color: var(--c-bg) !important;
}

/* 激活态 */
.el-menu-item.is-active {
  frontground-color: var(--front-color-primary) !important;
  /* 柔和浅绿色背景 */
  color: var(--font-color-primary) !important;
  /* 亮绿文字 */
  font-weight: 600;
  border-right: none !important;
  /* 去掉右边框 */
}



/* 图标颜色继承 */
.el-menu-item .el-icon {
  font-size: 18px;
}

.el-menu-item.is-active .el-icon {
  color: var(--font-color-primary) !important;
}

/* 下拉菜单去除 outline */
:deep(.el-dropdown *) {
  outline: none !important;
}
</style>
