-- ============================================
-- 修复数据库中的乱码数据
-- ============================================
-- 如果数据在存储时就已经是乱码，需要修复
-- 使用方法：docker exec -i rnd-lab-mysql mysql -u root -proot123 rnd_lab_management < fix-encoding-data.sql

USE rnd_lab_management;

-- 1. 检查当前数据
SELECT 
    id,
    username,
    HEX(full_name) as full_name_hex,
    full_name as full_name_current
FROM users 
WHERE username = 'admin';

-- 2. 修复 admin 用户的 full_name（如果数据损坏）
-- 注意：如果数据本身是正确的，这一步不需要执行
UPDATE users 
SET full_name = '系统管理员'
WHERE username = 'admin' 
  AND full_name != '系统管理员';

-- 3. 修复实验记录表的标题（示例）
-- 检查数据
SELECT 
    id,
    record_number,
    HEX(title) as title_hex,
    title as title_current
FROM experiment_records
LIMIT 5;

-- 如果标题是乱码，需要根据实际情况修复
-- UPDATE experiment_records SET title = '正确的标题' WHERE id = ?;

-- 4. 验证修复结果
SELECT 
    id,
    username,
    full_name,
    status
FROM users 
WHERE username = 'admin';


