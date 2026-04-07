import React, { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  Card,
  Form,
  Input,
  Select,
  DatePicker,
  Button,
  Space,
  message,
  Row,
  Col,
  InputNumber,
  Divider
} from 'antd'
import { SaveOutlined, ArrowLeftOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import api from '../utils/api'
import { useAuth } from '../contexts/AuthContext'

const { Option } = Select
const { TextArea } = Input

/**
 * 实验记录创建/编辑页面
 * 支持创建和编辑实验记录，包括属性数据
 */
const ExperimentForm = () => {
  const navigate = useNavigate()
  const { id } = useParams()
  const { user } = useAuth()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [options, setOptions] = useState({
    compositions: [],
    processes: [],
    teams: []
  })
  const [properties, setProperties] = useState([])

  const isEdit = !!id

  // 加载下拉选项数据
  useEffect(() => {
    loadOptions()
  }, [])

  // 如果是编辑模式，加载数据
  useEffect(() => {
    if (isEdit) {
      loadExperimentData()
    } else {
      // 创建模式：设置默认值
      form.setFieldsValue({
        status: 'DRAFT',
        experimentDate: dayjs(),
        teamId: user?.teamId
      })
    }
  }, [id, user])

  // 加载下拉选项
  const loadOptions = async () => {
    try {
      const response = await api.get('/common/options')
      setOptions(response.data)
    } catch (error) {
      console.error('加载选项数据失败:', error)
      message.error('加载选项数据失败')
    }
  }

  // 加载实验记录数据（编辑模式）
  const loadExperimentData = async () => {
    setLoading(true)
    try {
      const response = await api.get(`/experiments/${id}`)
      const data = response.data
      
      // 设置表单值
      form.setFieldsValue({
        recordNumber: data.recordNumber,
        title: data.title,
        description: data.description,
        compositionId: data.compositionId,
        processId: data.processId,
        experimentDate: data.experimentDate ? dayjs(data.experimentDate) : null,
        status: data.status || 'DRAFT',
        teamId: data.teamId
      })

      // 加载属性
      if (data.properties && data.properties.length > 0) {
        setProperties(data.properties.map((prop, index) => ({
          key: index,
          propertyName: prop.propertyName,
          propertyValue: prop.propertyValue,
          propertyUnit: prop.propertyUnit || '',
          propertyType: prop.propertyType || '',
          notes: prop.notes || ''
        })))
      }
    } catch (error) {
      console.error('加载实验记录失败:', error)
      message.error('加载实验记录失败')
      navigate('/experiments')
    } finally {
      setLoading(false)
    }
  }

  // 添加属性
  const addProperty = () => {
    setProperties([
      ...properties,
      {
        key: Date.now(),
        propertyName: '',
        propertyValue: null,
        propertyUnit: '',
        propertyType: '',
        notes: ''
      }
    ])
  }

  // 删除属性
  const removeProperty = (key) => {
    setProperties(properties.filter(item => item.key !== key))
  }

  // 更新属性
  const updateProperty = (key, field, value) => {
    setProperties(properties.map(item => 
      item.key === key ? { ...item, [field]: value } : item
    ))
  }

  // 提交表单
  const handleSubmit = async (values) => {
    setLoading(true)
    try {
      const payload = {
        ...values,
        experimentDate: values.experimentDate ? values.experimentDate.format('YYYY-MM-DD') : null,
        properties: properties
          .filter(prop => prop.propertyName && prop.propertyValue !== null && prop.propertyValue !== '')
          .map(prop => ({
            propertyName: prop.propertyName,
            propertyValue: prop.propertyValue,
            propertyUnit: prop.propertyUnit || null,
            propertyType: prop.propertyType || null,
            notes: prop.notes || null
          }))
      }

      if (isEdit) {
        // 更新
        await api.put(`/experiments/${id}`, payload)
        message.success('实验记录更新成功')
      } else {
        // 创建
        await api.post('/experiments', payload)
        message.success('实验记录创建成功')
      }

      // 返回列表
      navigate('/experiments')
    } catch (error) {
      console.error('保存失败:', error)
      const errorMessage = error.response?.data?.message || 
                          error.response?.data?.error ||
                          error.message || 
                          '保存失败'
      message.error(errorMessage)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <Card>
        <Space style={{ marginBottom: 16 }}>
          <Button
            icon={<ArrowLeftOutlined />}
            onClick={() => navigate('/experiments')}
          >
            返回列表
          </Button>
        </Space>

        <h1>{isEdit ? '编辑实验记录' : '创建实验记录'}</h1>

        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            status: 'DRAFT'
          }}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="实验编号"
                name="recordNumber"
                tooltip="不填写将自动生成"
              >
                <Input placeholder="如：EXP-2024-001" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="实验日期"
                name="experimentDate"
                rules={[{ required: true, message: '请选择实验日期' }]}
              >
                <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="实验标题"
            name="title"
            rules={[{ required: true, message: '请输入实验标题' }]}
          >
            <Input placeholder="请输入实验标题" />
          </Form.Item>

          <Form.Item
            label="实验描述"
            name="description"
          >
            <TextArea rows={4} placeholder="请输入实验描述" />
          </Form.Item>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                label="成分"
                name="compositionId"
              >
                <Select
                  placeholder="请选择成分"
                  allowClear
                  showSearch
                  filterOption={(input, option) =>
                    (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                  }
                >
                  {options.compositions.map(comp => (
                    <Option key={comp.id} value={comp.id}>
                      {comp.name} {comp.formula ? `(${comp.formula})` : ''}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="工艺"
                name="processId"
              >
                <Select
                  placeholder="请选择工艺"
                  allowClear
                  showSearch
                  filterOption={(input, option) =>
                    (option?.children ?? '').toLowerCase().includes(input.toLowerCase())
                  }
                >
                  {options.processes.map(proc => (
                    <Option key={proc.id} value={proc.id}>
                      {proc.name}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="状态"
                name="status"
                rules={[{ required: true, message: '请选择状态' }]}
              >
                <Select>
                  <Option value="DRAFT">草稿</Option>
                  <Option value="COMPLETED">已完成</Option>
                  <Option value="ARCHIVED">已归档</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label="所属团队"
            name="teamId"
          >
            <Select
              placeholder="请选择团队"
              allowClear
            >
              {options.teams.map(team => (
                <Option key={team.id} value={team.id}>
                  {team.teamName}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Divider>实验属性</Divider>

          <div>
            {properties.map(prop => (
              <Card
                key={prop.key}
                size="small"
                style={{ marginBottom: 8 }}
                extra={
                  <Button
                    type="link"
                    danger
                    icon={<DeleteOutlined />}
                    onClick={() => removeProperty(prop.key)}
                  >
                    删除
                  </Button>
                }
              >
                <Row gutter={16}>
                  <Col span={6}>
                    <Input
                      placeholder="属性名称（如：密度）"
                      value={prop.propertyName}
                      onChange={(e) => updateProperty(prop.key, 'propertyName', e.target.value)}
                    />
                  </Col>
                  <Col span={6}>
                    <InputNumber
                      placeholder="属性值"
                      value={prop.propertyValue}
                      onChange={(value) => updateProperty(prop.key, 'propertyValue', value)}
                      style={{ width: '100%' }}
                      precision={2}
                    />
                  </Col>
                  <Col span={4}>
                    <Input
                      placeholder="单位"
                      value={prop.propertyUnit}
                      onChange={(e) => updateProperty(prop.key, 'propertyUnit', e.target.value)}
                    />
                  </Col>
                  <Col span={4}>
                    <Input
                      placeholder="类型"
                      value={prop.propertyType}
                      onChange={(e) => updateProperty(prop.key, 'propertyType', e.target.value)}
                    />
                  </Col>
                  <Col span={4}>
                    <Input
                      placeholder="备注"
                      value={prop.notes}
                      onChange={(e) => updateProperty(prop.key, 'notes', e.target.value)}
                    />
                  </Col>
                </Row>
              </Card>
            ))}

            <Button
              type="dashed"
              onClick={addProperty}
              icon={<PlusOutlined />}
              block
              style={{ marginBottom: 16 }}
            >
              添加属性
            </Button>
          </div>

          <Form.Item>
            <Space>
              <Button
                type="primary"
                htmlType="submit"
                icon={<SaveOutlined />}
                loading={loading}
              >
                {isEdit ? '更新' : '创建'}
              </Button>
              <Button onClick={() => navigate('/experiments')}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  )
}

export default ExperimentForm


