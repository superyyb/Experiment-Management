-- ============================================
-- 初始化测试数据
-- ============================================

USE rnd_lab_management;

-- 插入测试用户
INSERT INTO users (username, password, email, full_name, team_id) VALUES
('researcher1', '$2b$10$UfoZnNnr/e4eYUeaT8Y9H.eyqhbYU3.QfMSn.0l0vfEn09zf82lbq', 'researcher1@example.com', '研究员1', 1),
('researcher2', '$2b$10$UfoZnNnr/e4eYUeaT8Y9H.eyqhbYU3.QfMSn.0l0vfEn09zf82lbq', 'researcher2@example.com', '研究员2', 2),
('viewer1', '$2b$10$UfoZnNnr/e4eYUeaT8Y9H.eyqhbYU3.QfMSn.0l0vfEn09zf82lbq', 'viewer1@example.com', '查看者1', 3);

-- 分配角色
INSERT INTO user_roles (user_id, role_id) VALUES
(2, 2), (3, 2), (4, 3);

-- 插入测试成分
INSERT INTO compositions (name, formula, category, description, created_by) VALUES
('氧化铝', 'Al2O3', '氧化物', '高纯度氧化铝粉末', 1),
('碳化硅', 'SiC', '碳化物', '碳化硅陶瓷材料', 1),
('氮化硅', 'Si3N4', '氮化物', '氮化硅陶瓷材料', 1),
('氧化锆', 'ZrO2', '氧化物', '氧化锆稳定陶瓷', 2);

-- 插入测试工艺
INSERT INTO processes (name, process_type, temperature, pressure, duration, description, created_by) VALUES
('高温烧结', 'SINTERING', 1600.00, 101325.00, 120, '高温烧结工艺，温度1600℃，持续2小时', 1),
('热压成型', 'HOT_PRESSING', 1500.00, 20000000.00, 60, '热压成型工艺，温度1500℃，压力20MPa', 1),
('等静压', 'CIP', 25.00, 300000000.00, 30, '冷等静压工艺，室温，压力300MPa', 2);

-- 插入测试实验记录
INSERT INTO experiment_records (record_number, title, description, composition_id, process_id, experiment_date, status, team_id, created_by) VALUES
('EXP-2024-001', '氧化铝陶瓷高温烧结实验', '研究氧化铝在不同温度下的烧结性能', 1, 1, '2024-01-15', 'COMPLETED', 1, 1),
('EXP-2024-002', '碳化硅热压成型实验', '探索碳化硅热压成型的最佳工艺参数', 2, 2, '2024-01-20', 'COMPLETED', 1, 2),
('EXP-2024-003', '氮化硅等静压实验', '研究氮化硅等静压成型工艺', 3, 3, '2024-02-01', 'DRAFT', 2, 3),
('EXP-2024-004', '氧化锆高温烧结实验', '氧化锆陶瓷的高温烧结性能研究', 4, 1, '2024-02-10', 'COMPLETED', 1, 1);

-- 插入测试属性
INSERT INTO properties (experiment_id, property_name, property_value, property_unit, property_type, notes) VALUES
(1, '密度', 3.95, 'g/cm³', '物理', '烧结后密度'),
(1, '抗弯强度', 380.5, 'MPa', '机械', '三点弯曲测试'),
(1, '硬度', 9.2, 'Mohs', '机械', '维氏硬度'),
(2, '密度', 3.21, 'g/cm³', '物理', '热压后密度'),
(2, '抗弯强度', 450.8, 'MPa', '机械', '四点弯曲测试'),
(2, '热导率', 120.5, 'W/(m·K)', '物理', '室温热导率'),
(4, '密度', 6.08, 'g/cm³', '物理', '烧结后密度'),
(4, '抗弯强度', 850.2, 'MPa', '机械', '三点弯曲测试');

-- 插入实验记录-成分关联
INSERT INTO experiment_compositions (experiment_id, composition_id, ratio) VALUES
(1, 1, 100.0000),
(2, 2, 100.0000),
(3, 3, 100.0000),
(4, 4, 100.0000);

