import axios from 'axios'

/**
 * Axios实例配置
 * 统一处理请求和响应
 */
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  },
  responseType: 'json',
  responseEncoding: 'utf8'
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('请求已添加Authorization头:', config.url)
    } else {
      console.warn('未找到token，请求可能失败:', config.url)
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // 记录错误详情，方便调试
    console.error('API请求失败:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    })
    
    if (error.response?.status === 401) {
      // 未授权，清除token并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/login'
    } else if (error.response?.status === 403) {
      // 权限不足
      console.error('权限不足，可能需要重新登录')
    }
    
    return Promise.reject(error)
  }
)

export default api

