-- ============================================
-- 数据库连接和用户检查脚本
-- ============================================
-- 使用方法：mysql -u root -proot123 < check-database.sql
-- 或者在 MySQL 客户端中执行此脚本

USE rnd_lab_management;

-- 1. 检查数据库是否存在
SELECT 
    SCHEMA_NAME as '数据库名称',
    DEFAULT_CHARACTER_SET_NAME as '字符集',
    DEFAULT_COLLATION_NAME as '排序规则'
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'rnd_lab_management';

-- 2. 检查 admin 用户是否存在
SELECT 
    id,
    username,
    email,
    full_name as '全名',
    team_id as '团队ID',
    status,
    CASE 
        WHEN status = 1 THEN '活跃' 
        WHEN status = 0 THEN '禁用' 
        ELSE '未知' 
    END as '状态',
    created_at as '创建时间',
    updated_at as '更新时间'
FROM users 
WHERE username = 'admin';

-- 3. 检查 admin 用户的角色
SELECT 
    u.id as '用户ID',
    u.username as '用户名',
    r.id as '角色ID',
    r.role_name as '角色名称',
    r.description as '角色描述'
FROM users u
INNER JOIN user_roles ur ON u.id = ur.user_id
INNER JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'admin';

-- 4. 检查所有用户
SELECT 
    id,
    username,
    email,
    full_name,
    status,
    created_at
FROM users
ORDER BY id;

-- 5. 检查数据库连接信息（显示当前连接）
SELECT 
    DATABASE() as '当前数据库',
    USER() as '当前用户',
    CONNECTION_ID() as '连接ID',
    VERSION() as 'MySQL版本';

-- 6. 检查表是否存在
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '行数',
    TABLE_COLLATION as '排序规则'
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'rnd_lab_management'
ORDER BY TABLE_NAME;


