import React from 'react'
import { Card, Row, Col, Statistic } from 'antd'
import { ExperimentOutlined, CheckCircleOutlined, ClockCircleOutlined } from '@ant-design/icons'

/**
 * 仪表盘页面
 * 显示系统概览统计信息
 */
const Dashboard = () => {
  return (
    <div>
      <h1>仪表盘</h1>
      <p style={{ color: '#666', marginBottom: 8 }}>
        全栈实验记录平台（50,000+ 规模）：Spring Boot + MyBatis + React，Redis 缓存与 RBAC
      </p>
      <Row gutter={16} style={{ marginTop: 24 }}>
        <Col span={8}>
          <Card>
            <Statistic
              title="总实验记录（规模）"
              value={50000}
              suffix="+"
              prefix={<ExperimentOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="已完成（示例占比）"
              value={41500}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic
              title="进行中（示例占比）"
              value={8500}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default Dashboard

