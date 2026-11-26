import { createRouter, createWebHistory } from 'vue-router'
import { projectName } from '../config/config.default.js'
import request from '@/utils/request'

const routes = [
  // 根路径：统一交给守卫处理
  {
    path: '/',
    redirect: '/login'
  },

  // 通用路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('../views/404.vue'),
    meta: { title: '页面不存在' }
  },

  // 后台路由
  {
    path: '/front',
    name: 'Front',
    component: () => import('../views/Front.vue/index.js'),
    redirect: '/front/square',          // 访问 /front 自动进首页
    meta: { requiresAuth: true },    // 标记该分支需要登录
    children: [

      {
        path: 'password',
        name: 'FrontPassword',
        component: () => import('../views/front/Password.vue'),
        meta: { title: '修改密码' }
      },
      {
        path: 'person',
        name: 'FrontPerson',
        component: () => import('../views/front/Person.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'post',
        name: 'FrontPost',
        component: () => import('../views/front/Post.vue'),
        meta: { title: '动态详情页' }
      },
      {
        path: 'following',
        name: 'FrontFollowing',
        component: () => import('../views/front/Following.vue'),
        meta: { title: '院系管理' }
      },
      {
        path: 'nearby',
        name: 'FrontNearby',
        component: () => import('../views/front/Nearby.vue'),
        meta: { title: '院系管理' }
      },
      {
        path: 'profile',
        name: 'FrontProfile',
        component: () => import('../views/front/Profile.vue'),
        meta: { title: '院系管理' }
      },
      {
        path: 'publish',
        name: 'FrontPublish',
        component: () => import('../views/front/Publish.vue'),
        meta: { title: '院系管理' }
      },
      {
        path: 'relations',
        name: 'FrontRelations',
        component: () => import('../views/front/Relations.vue'),
        meta: { title: '院系管理' }
      },
      {
        path: 'search',
        name: 'FrontSearch',
        component: () => import('../views/front/Search.vue'),
        meta: { title: '院系管理' }
      },
      {
        path: 'square',
        name: 'FrontSquare',
        component: () => import('../views/front/Square.vue'),
        meta: { title: '院系管理' }
      },
    ]
  },

  // 兜底：所有未匹配路由 → 404
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  // 切换路由时滚动到顶部；后退时恢复位置
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  }
})

// 安全读取登录信息，避免 JSON.parse 抛错
function getSysuser() {
  try {
    const str = localStorage.getItem('sysuser')
    return str ? JSON.parse(str) : null
  } catch {
    return null
  }
}

// 全局前置守卫
router.beforeEach(async (to) => {
  const sysuser = getSysuser()

  // 1. 【白名单】如果是登录页或注册页，直接放行！
  if (to.path === '/login' || to.path === '/register') {
    return true
  }

  // 2. 如果去后台（requiresAuth），但没 token，去登录
  if (to.meta.requiresAuth && !sysuser?.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  // 3. 如果有 token，且去的是后台页面，才去调后端验证
  if (to.meta.requiresAuth && sysuser?.token) {
    try {
      // 加上 timeout 防止后端卡死导致前端一直等
      await request.get('/web/checkToken', { timeout: 3000 })
      return true
    } catch (e) {
      // 验证失败，直接去登录页
      return { path: '/login', query: { redirect: to.fullPath } }
    }
  }

  // 其他情况全部放行
  return true
})
// 全局后置守卫
router.afterEach((to) => {
  const title = to.meta.title
  document.title = title ? `${title}-${projectName}` : projectName
})

export default router