import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import ExperimentList from './pages/ExperimentList'
import ExperimentDetail from './pages/ExperimentDetail'
import ExperimentForm from './pages/ExperimentForm'
import PrivateRoute from './components/PrivateRoute'
import Layout from './components/Layout'

/**
 * 主应用组件
 * 配置路由和认证上下文
 */
function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route
            path="/"
            element={
              <PrivateRoute>
                <Layout />
              </PrivateRoute>
            }
          >
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="experiments" element={<ExperimentList />} />
            <Route path="experiments/new" element={<ExperimentForm />} />
            <Route path="experiments/:id" element={<ExperimentDetail />} />
            <Route path="experiments/:id/edit" element={<ExperimentForm />} />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  )
}

export default App

