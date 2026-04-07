import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Table,
  Card,
  Input,
  Button,
  Space,
  DatePicker,
  Select,
  Row,
  Col,
  Tag,
  message
} from 'antd'
import { SearchOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api from '../utils/api'

const { RangePicker } = DatePicker
const { Option } = Select

/**
 * 实验记录列表页面
 * 实现高级搜索功能：过滤、范围、模糊匹配、排序
 */
const ExperimentList = () => {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
    total: 0
  })
  const [filters, setFilters] = useState({
    keyword: '',
    status: '',
    startDate: null,
    endDate: null,
    sortBy: 'createdAt',
    sortOrder: 'DESC'
  })

  // 加载数据
  const loadData = async (page = 1, pageSize = 20) => {
    setLoading(true)
    try {
      const params = {
        page,
        pageSize,
        ...filters
      }
      
      // 处理日期范围
      if (filters.startDate && filters.endDate) {
        params.startDate = filters.startDate.format('YYYY-MM-DD')
        params.endDate = filters.endDate.format('YYYY-MM-DD')
      }
      
      const response = await api.get('/experiments/search', { params })
      const result = response.data
      
      setData(result.data)
      setPagination({
        current: result.page,
        pageSize: result.pageSize,
        total: result.total
      })
    } catch (error) {
      console.error('加载数据失败:', error)
      const errorMessage = error.response?.data?.message || 
                          error.response?.status === 403 ? '权限不足，请重新登录' :
                          error.response?.status === 401 ? '未授权，请重新登录' :
                          error.message || '加载数据失败'
      message.error(errorMessage)
      
      // 如果是权限错误，尝试重新登录
      if (error.response?.status === 401 || error.response?.status === 403) {
        setTimeout(() => {
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          window.location.href = '/login'
        }, 2000)
      }
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  // 搜索
  const handleSearch = () => {
    loadData(1, pagination.pageSize)
  }

  // 重置
  const handleReset = () => {
    setFilters({
      keyword: '',
      status: '',
      startDate: null,
      endDate: null,
      sortBy: 'createdAt',
      sortOrder: 'DESC'
    })
    setTimeout(() => {
      loadData(1, pagination.pageSize)
    }, 100)
  }

  // 表格列定义
  const columns = [
    {
      title: '实验编号',
      dataIndex: 'recordNumber',
      key: 'recordNumber',
      sorter: true
    },
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      ellipsis: true
    },
    {
      title: '成分',
      key: 'composition',
      render: (_, record) => record.composition?.name || '-'
    },
    {
      title: '工艺',
      key: 'process',
      render: (_, record) => record.process?.name || '-'
    },
    {
      title: '实验日期',
      dataIndex: 'experimentDate',
      key: 'experimentDate',
      render: (date) => date ? dayjs(date).format('YYYY-MM-DD') : '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        const colorMap = {
          'DRAFT': 'orange',
          'COMPLETED': 'green',
          'ARCHIVED': 'gray'
        }
        const textMap = {
          'DRAFT': '草稿',
          'COMPLETED': '已完成',
          'ARCHIVED': '已归档'
        }
        return <Tag color={colorMap[status]}>{textMap[status] || status}</Tag>
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date) => dayjs(date).format('YYYY-MM-DD HH:mm:ss')
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Button
          type="link"
          onClick={() => navigate(`/experiments/${record.id}`)}
        >
          查看详情
        </Button>
      )
    }
  ]

  return (
    <div>
      <Card>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <div>
            <h1 style={{ margin: 0 }}>实验记录管理</h1>
            <div style={{ color: '#888', fontSize: 13, marginTop: 4 }}>
              高级检索 · Redis 缓存热点查询 · 支撑 50,000+ 级数据协作
            </div>
          </div>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate('/experiments/new')}
          >
            创建实验记录
          </Button>
        </div>
        
        {/* 搜索表单 */}
        <Card size="small" style={{ marginBottom: 16 }}>
          <Row gutter={16}>
            <Col span={6}>
              <Input
                placeholder="关键词搜索（标题、描述）"
                value={filters.keyword}
                onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
                onPressEnter={handleSearch}
              />
            </Col>
            <Col span={4}>
              <Select
                placeholder="状态"
                value={filters.status}
                onChange={(value) => setFilters({ ...filters, status: value })}
                allowClear
                style={{ width: '100%' }}
              >
                <Option value="DRAFT">草稿</Option>
                <Option value="COMPLETED">已完成</Option>
                <Option value="ARCHIVED">已归档</Option>
              </Select>
            </Col>
            <Col span={6}>
              <RangePicker
                style={{ width: '100%' }}
                value={[filters.startDate, filters.endDate]}
                onChange={(dates) => setFilters({
                  ...filters,
                  startDate: dates?.[0] || null,
                  endDate: dates?.[1] || null
                })}
              />
            </Col>
            <Col span={4}>
              <Select
                placeholder="排序"
                value={`${filters.sortBy}-${filters.sortOrder}`}
                onChange={(value) => {
                  const [sortBy, sortOrder] = value.split('-')
                  setFilters({ ...filters, sortBy, sortOrder })
                }}
                style={{ width: '100%' }}
              >
                <Option value="createdAt-DESC">创建时间（降序）</Option>
                <Option value="createdAt-ASC">创建时间（升序）</Option>
                <Option value="experimentDate-DESC">实验日期（降序）</Option>
                <Option value="experimentDate-ASC">实验日期（升序）</Option>
                <Option value="recordNumber-ASC">实验编号（升序）</Option>
              </Select>
            </Col>
            <Col span={4}>
              <Space>
                <Button
                  type="primary"
                  icon={<SearchOutlined />}
                  onClick={handleSearch}
                >
                  搜索
                </Button>
                <Button
                  icon={<ReloadOutlined />}
                  onClick={handleReset}
                >
                  重置
                </Button>
              </Space>
            </Col>
          </Row>
        </Card>

        {/* 数据表格 */}
        <Table
          columns={columns}
          dataSource={data}
          rowKey="id"
          loading={loading}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条记录`,
            onChange: (page, pageSize) => {
              loadData(page, pageSize)
            }
          }}
        />
      </Card>
    </div>
  )
}

export default ExperimentList

