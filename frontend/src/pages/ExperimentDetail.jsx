import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Card, Descriptions, Tag, Button, Table, Spin, message, Space } from 'antd'
import { ArrowLeftOutlined, EditOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api from '../utils/api'
import { useAuth } from '../contexts/AuthContext'

/**
 * 实验记录详情页面
 * 显示实验记录的完整信息
 */
const ExperimentDetail = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [loading, setLoading] = useState(false)
  const [record, setRecord] = useState(null)
  
  // 检查是否有编辑权限（ADMIN, RESEARCHER, TEAM_LEADER）
  const canEdit = user?.roles?.some(role => 
    ['ADMIN', 'RESEARCHER', 'TEAM_LEADER'].includes(role)
  ) || false

  useEffect(() => {
    loadData()
  }, [id])

  const loadData = async () => {
    setLoading(true)
    try {
      const response = await api.get(`/experiments/${id}`)
      setRecord(response.data)
    } catch (error) {
      message.error('加载数据失败')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: 50 }}>
        <Spin size="large" />
      </div>
    )
  }

  if (!record) {
    return <div>数据不存在</div>
  }

  const statusColorMap = {
    'DRAFT': 'orange',
    'COMPLETED': 'green',
    'ARCHIVED': 'gray'
  }

  const statusTextMap = {
    'DRAFT': '草稿',
    'COMPLETED': '已完成',
    'ARCHIVED': '已归档'
  }

  const propertyColumns = [
    {
      title: '属性名称',
      dataIndex: 'propertyName',
      key: 'propertyName'
    },
    {
      title: '属性值',
      dataIndex: 'propertyValue',
      key: 'propertyValue'
    },
    {
      title: '单位',
      dataIndex: 'propertyUnit',
      key: 'propertyUnit'
    },
    {
      title: '类型',
      dataIndex: 'propertyType',
      key: 'propertyType'
    },
    {
      title: '备注',
      dataIndex: 'notes',
      key: 'notes'
    }
  ]

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button
          icon={<ArrowLeftOutlined />}
          onClick={() => navigate('/experiments')}
        >
          返回列表
        </Button>
        {canEdit && (
          <Button
            type="primary"
            icon={<EditOutlined />}
            onClick={() => navigate(`/experiments/${id}/edit`)}
          >
            编辑
          </Button>
        )}
      </Space>

      <Card title="实验记录详情">
        <Descriptions bordered column={2}>
          <Descriptions.Item label="实验编号">{record.recordNumber}</Descriptions.Item>
          <Descriptions.Item label="状态">
            <Tag color={statusColorMap[record.status]}>
              {statusTextMap[record.status] || record.status}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="标题" span={2}>
            {record.title}
          </Descriptions.Item>
          <Descriptions.Item label="描述" span={2}>
            {record.description || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="成分">
            {record.composition?.name || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="工艺">
            {record.process?.name || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="实验日期">
            {record.experimentDate ? dayjs(record.experimentDate).format('YYYY-MM-DD') : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="团队">
            {record.team?.teamName || '-'}
          </Descriptions.Item>
          {record.process && (
            <>
              <Descriptions.Item label="温度">
                {record.process.temperature ? `${record.process.temperature}℃` : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="压力">
                {record.process.pressure ? `${record.process.pressure}Pa` : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="持续时间">
                {record.process.duration ? `${record.process.duration}分钟` : '-'}
              </Descriptions.Item>
            </>
          )}
          <Descriptions.Item label="创建时间">
            {dayjs(record.createdAt).format('YYYY-MM-DD HH:mm:ss')}
          </Descriptions.Item>
          <Descriptions.Item label="更新时间">
            {dayjs(record.updatedAt).format('YYYY-MM-DD HH:mm:ss')}
          </Descriptions.Item>
        </Descriptions>

        {record.properties && record.properties.length > 0 && (
          <Card title="属性列表" style={{ marginTop: 24 }}>
            <Table
              columns={propertyColumns}
              dataSource={record.properties}
              rowKey="id"
              pagination={false}
            />
          </Card>
        )}
      </Card>
    </div>
  )
}

export default ExperimentDetail

