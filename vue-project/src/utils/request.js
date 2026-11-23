import axios from 'axios'
import { serverHost } from '../config/config.default.js'
import { ElMessage } from 'element-plus'
import router from '@/router/index.js'

// 安全读取登录信息，避免 JSON.parse 抛错
function getSysuser() {
  try {
    // 统一读取 'sysuser'，与你 login 代码中的 localStorage.setItem('sysuser') 保持一致
    const str = localStorage.getItem('sysuser')
    return str ? JSON.parse(str) : null
  } catch {
    return null
  }
}

const request = axios.create({
  baseURL: serverHost,
  timeout: 5000
})

// ==================== 请求拦截器 ====================
request.interceptors.request.use(
  (config) => {
    if (config.data instanceof FormData) {
      // FormData 让浏览器自己带 boundary，删掉手动的 Content-Type
      delete config.headers['Content-Type']
    } else {
      config.headers['Content-Type'] = 'application/json;charset=utf-8'
    }

    // 自动携带 token
    const sysuser = getSysuser()
    // 注意：这里需要确保你登录成功后存入的 res.data 中确实包含 token 字段
    if (sysuser && sysuser.token) {
      config.headers['token'] = sysuser.token
    }

    return config
  },
  (error) => {
    console.error('请求拦截器错误：', error)
    return Promise.reject(error)
  }
)

// ==================== 响应拦截器 ====================
request.interceptors.response.use(
  (response) => {
    let res = response.data

    // 文件流直接返回
    if (response.config.responseType === 'blob') {
      return res
    }

    // 兼容后端返回字符串的情况
    if (typeof res === 'string') {
      try {
        res = res ? JSON.parse(res) : res
      } catch {
        // 解析失败就原样返回，交给调用方处理
        return res
      }
    }

    // --- 针对后端 Result 类的核心处理逻辑 ---

    // 1. 未登录 / token 失效 (兼容数字和字符串形式的 401)
    if (res.code === 401 || res.code === '401') {
      ElMessage.error(res.msg || '登录已失效，请重新登录')

      // 清除本地登录信息
      localStorage.removeItem('sysuser')

      // 避免在登录页重复跳转
      if (router.currentRoute.value.path !== '/login') {
        router.push({
          path: '/login',
          query: { redirect: router.currentRoute.value.fullPath }
        })
      }

      // ⚠️ 阻断后续代码
      return Promise.reject(new Error(res.msg || '未登录'))
    }

    // 2. 业务成功 (根据你的 Result 类，code === 1 为成功)
    if (res.code === 1) {
      return res
    }

    // 3. 业务失败 (根据你的 Result 类，code === 0 或其他均为失败)
    // 统一弹出错误提示
    ElMessage.error(res.msg || '请求失败')
    
    // ⚠️ 关键修复：必须 return Promise.reject，阻断业务代码里的 .then 继续执行
    return Promise.reject(new Error(res.msg || 'Error'))
  },
  (error) => {
    console.error('响应错误：', error)

    // 统一 HTTP 错误提示
    let msg = '网络异常，请稍后重试'
    if (error.response) {
      // 有响应，但状态码非 2xx
      const status = error.response.status
      if (status === 401) {
        msg = '登录已失效，请重新登录'
        localStorage.removeItem('sysuser') // 统一使用 sysuser
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
      } else if (status === 403) {
        msg = '没有权限访问'
      } else if (status === 404) {
        msg = '请求的资源不存在'
      } else if (status >= 500) {
        msg = '服务器异常，请稍后重试'
      } else {
        // 尝试读取后端返回的具体错误信息
        msg = error.response.data?.msg || `请求失败（${status}）`
      }
    } else if (error.code === 'ECONNABORTED') {
      msg = '请求超时，请稍后重试'
    } else if (error.message?.includes('Network Error')) {
      msg = '网络连接失败，请检查网络'
    }

    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request