-- ============================================
-- 用户和角色管理表（RBAC）
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '邮箱',
    full_name VARCHAR(100) COMMENT '全名',
    team_id BIGINT COMMENT '所属团队ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-活跃，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_team_id (team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 团队表
CREATE TABLE IF NOT EXISTS teams (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_name VARCHAR(100) NOT NULL COMMENT '团队名称',
    description VARCHAR(255) COMMENT '团队描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_team_name (team_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队表';

-- ============================================
-- 核心业务表：成分、工艺、属性
-- ============================================

-- 成分表（Composition）
CREATE TABLE IF NOT EXISTS compositions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '成分名称',
    formula VARCHAR(100) COMMENT '化学式',
    category VARCHAR(50) COMMENT '成分类别',
    description TEXT COMMENT '成分描述',
    created_by BIGINT COMMENT '创建人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_created_by (created_by),
    FULLTEXT INDEX ft_name_desc (name, description),
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成分表';

-- 工艺表（Process）
CREATE TABLE IF NOT EXISTS processes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '工艺名称',
    process_type VARCHAR(50) COMMENT '工艺类型',
    temperature DECIMAL(10, 2) COMMENT '温度（℃）',
    pressure DECIMAL(15, 2) COMMENT '压力（Pa）',
    duration INT COMMENT '持续时间（分钟）',
    description TEXT COMMENT '工艺描述',
    created_by BIGINT COMMENT '创建人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_process_type (process_type),
    INDEX idx_temperature (temperature),
    INDEX idx_created_by (created_by),
    FULLTEXT INDEX ft_name_desc (name, description),
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工艺表';

-- 实验记录表（Experiment Record）- 核心表
CREATE TABLE IF NOT EXISTS experiment_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    record_number VARCHAR(50) UNIQUE NOT NULL COMMENT '实验编号',
    title VARCHAR(300) NOT NULL COMMENT '实验标题',
    description TEXT COMMENT '实验描述',
    composition_id BIGINT COMMENT '主要成分ID',
    process_id BIGINT COMMENT '工艺ID',
    experiment_date DATE COMMENT '实验日期',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，COMPLETED-完成，ARCHIVED-归档',
    team_id BIGINT COMMENT '所属团队ID',
    created_by BIGINT COMMENT '创建人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_record_number (record_number),
    INDEX idx_experiment_date (experiment_date),
    INDEX idx_status (status),
    INDEX idx_composition_id (composition_id),
    INDEX idx_process_id (process_id),
    INDEX idx_team_id (team_id),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at),
    FULLTEXT INDEX ft_title_desc (title, description),
    FOREIGN KEY (composition_id) REFERENCES compositions(id) ON DELETE SET NULL,
    FOREIGN KEY (process_id) REFERENCES processes(id) ON DELETE SET NULL,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验记录表';

-- 属性表（Properties）
CREATE TABLE IF NOT EXISTS properties (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    experiment_id BIGINT NOT NULL COMMENT '实验记录ID',
    property_name VARCHAR(100) NOT NULL COMMENT '属性名称',
    property_value DECIMAL(15, 4) COMMENT '属性数值',
    property_unit VARCHAR(20) COMMENT '单位',
    property_type VARCHAR(50) COMMENT '属性类型：物理、化学、机械等',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_experiment_id (experiment_id),
    INDEX idx_property_name (property_name),
    INDEX idx_property_type (property_type),
    INDEX idx_property_value (property_value),
    FOREIGN KEY (experiment_id) REFERENCES experiment_records(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='属性表';

-- 实验记录-成分关联表（多对多关系）
CREATE TABLE IF NOT EXISTS experiment_compositions (
    experiment_id BIGINT NOT NULL,
    composition_id BIGINT NOT NULL,
    ratio DECIMAL(10, 4) COMMENT '比例',
    PRIMARY KEY (experiment_id, composition_id),
    INDEX idx_experiment_id (experiment_id),
    INDEX idx_composition_id (composition_id),
    FOREIGN KEY (experiment_id) REFERENCES experiment_records(id) ON DELETE CASCADE,
    FOREIGN KEY (composition_id) REFERENCES compositions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验记录-成分关联表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认角色
INSERT INTO roles (role_name, description) VALUES
('ADMIN', '系统管理员，拥有所有权限'),
('RESEARCHER', '研究员，可以创建和查看实验记录'),
('VIEWER', '查看者，只能查看实验记录'),
('TEAM_LEADER', '团队负责人，可以管理团队实验记录');

-- 插入默认团队
INSERT INTO teams (team_name, description) VALUES
('材料研发团队', '负责新材料研发'),
('工艺优化团队', '负责工艺优化'),
('数据分析团队', '负责数据分析');

-- 插入默认管理员用户（密码：admin123，实际使用时需要加密）
-- 注意：实际部署时应该使用BCrypt加密后的密码
INSERT INTO users (username, password, email, full_name, team_id) VALUES
('admin', '$2b$10$UfoZnNnr/e4eYUeaT8Y9H.eyqhbYU3.QfMSn.0l0vfEn09zf82lbq', 'admin@example.com', '系统管理员', 1);

-- 分配管理员角色
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1);

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

