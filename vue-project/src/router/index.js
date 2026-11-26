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
    path: '/back',
    name: 'Back',
    component: () => import('../views/Back.vue'),
    redirect: '/back/home',          // 访问 /back 自动进首页
    meta: { requiresAuth: true },    // 标记该分支需要登录
    children: [
      {
        path: 'home',
        name: 'BackHome',
        component: () => import('../views/front/Home.vue'),
        meta: { title: '后台首页' }
      },
      {
        path: 'password',
        name: 'BackPassword',
        component: () => import('../views/front/Password.vue'),
        meta: { title: '修改密码' }
      },
      {
        path: 'person',
        name: 'BackPerson',
        component: () => import('../views/front/Person.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'admin',
        name: 'BackAdmin',
        component: () => import('../views/front/Admin.vue'),
        meta: { title: '管理员管理' }
      },
      {
        path: 'dept',
        name: 'BackDept',
        component: () => import('../views/back/Dept.vue'),
        meta: { title: '院系管理' }
      },
      {
        path: 'student',
        name: 'BackStudent',
        component: () => import('../views/front/Student.vue'),
        meta: { title: '学生管理' }
      },
      {
        path: 'teacher',
        name: 'BackTeacher',
        component: () => import('../views/back/Teacher.vue'),
        meta: { title: '教师管理' }
      },
      {
        path: 'class',
        name: 'BackClass',
        component: () => import('../views/back/Class.vue'),
        meta: { title: '班级管理' }
      },
      {
        path: 'course',
        name: 'BackCourse',
        component: () => import('../views/back/Course.vue'),
        meta: { title: '课程管理' }
      },
      {
        path: 'teaching',
        name: 'BackTeaching',
        component: () => import('../views/back/Teaching.vue'),
        meta: { title: '授课安排管理' }
      },
      {
        path: 'enrollment',
        name: 'BackEnrollment',
        component: () => import('../views/back/Enrollment.vue'),
        meta: { title: '学生选课管理' }
      }
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
  // 千万别在这里加“已登录就跳后台”，容易和401清理冲突导致死循环
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
      // ⚠️ 注意：不要传 sysuser 作为第二个参数，token 拦截器已经帮你带了
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