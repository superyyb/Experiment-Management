import React, { createContext, useContext, useState, useEffect } from 'react'
import { message } from 'antd'
import api from '../utils/api'

/**
 * 认证上下文
 * 管理用户登录状态和token
 */
const AuthContext = createContext()

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // 从localStorage加载token
  useEffect(() => {
    const token = localStorage.getItem('token')
    const userInfo = localStorage.getItem('userInfo')
    if (token && userInfo) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
      setUser(JSON.parse(userInfo))
    }
    setLoading(false)
  }, [])

  // 登录
  const login = async (username, password) => {
    try {
      const response = await api.post('/auth/login', { username, password })
      const { token, ...userInfo } = response.data
      
      // 保存token和用户信息
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`
      
      setUser(userInfo)
      message.success('登录成功')
      return true
    } catch (error) {
      message.error(error.response?.data?.message || '登录失败')
      return false
    }
  }

  // 登出
  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    delete api.defaults.headers.common['Authorization']
    setUser(null)
    message.success('已退出登录')
  }

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

