-- ============================================
-- 修复 admin 用户密码
-- 密码：admin123
-- ============================================
-- 
-- 重要提示：
-- BCrypt 每次生成的 hash 都不同，但都可以验证同一个密码。
-- 如果直接执行此脚本后仍然无法登录，请使用以下方法：
--
-- 方法1：使用后端测试端点生成密码hash（推荐）
-- 1. 启动后端服务
-- 2. 访问 http://localhost:8080/api/common/test/bcrypt
-- 3. 复制返回的 bcrypt_hash 值
-- 4. 执行：UPDATE users SET password = '<复制的hash>' WHERE username = 'admin';
--
-- 方法2：重新初始化数据库
-- 执行 database/schema.sql 和 database/init-data.sql
--
-- ============================================

USE rnd_lab_management;

-- 方法：使用一个已知可以验证 admin123 的 BCrypt hash
-- 注意：如果这个hash不工作，请使用方法1生成新的hash
-- 这个hash是通过 Spring Security BCryptPasswordEncoder 生成的
UPDATE users 
SET password = '$2b$10$UfoZnNnr/e4eYUeaT8Y9H.eyqhbYU3.QfMSn.0l0vfEn09zf82lbq' 
WHERE username = 'admin';

-- 如果 admin 用户不存在，则创建
INSERT INTO users (username, password, email, full_name, team_id, status) 
SELECT 
    'admin', 
    '$2b$10$UfoZnNnr/e4eYUeaT8Y9H.eyqhbYU3.QfMSn.0l0vfEn09zf82lbq', 
    'admin@example.com', 
    '系统管理员', 
    1,
    1
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- 确保 admin 用户有管理员角色
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.role_name = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur 
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- 验证：检查 admin 用户是否存在且状态为活跃
SELECT 
    id, 
    username, 
    email, 
    full_name, 
    status,
    CASE WHEN status = 1 THEN '活跃' ELSE '禁用' END AS status_text
FROM users 
WHERE username = 'admin';

