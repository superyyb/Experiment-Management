-- ============================================
-- 修复数据库中已损坏的乱码数据
-- ============================================
-- 问题：数据在存储时使用了错误的字符集（latin1），导致乱码
-- 解决方法：将错误编码的数据转换回正确的 UTF-8 编码
-- 
-- 使用方法：
-- docker exec -i rnd-lab-mysql mysql -u root -proot123 rnd_lab_management < fix-corrupted-data.sql

USE rnd_lab_management;

-- 1. 修复 users 表中的 full_name 字段
UPDATE users 
SET full_name = CONVERT(CAST(CONVERT(full_name USING latin1) AS BINARY) USING utf8mb4)
WHERE username = 'admin'
  AND full_name != CONVERT(CAST(CONVERT(full_name USING latin1) AS BINARY) USING utf8mb4);

-- 2. 修复 experiment_records 表中的 title 字段
UPDATE experiment_records
SET title = CONVERT(CAST(CONVERT(title USING latin1) AS BINARY) USING utf8mb4)
WHERE title != CONVERT(CAST(CONVERT(title USING latin1) AS BINARY) USING utf8mb4);

-- 3. 修复 experiment_records 表中的 description 字段
UPDATE experiment_records
SET description = CONVERT(CAST(CONVERT(description USING latin1) AS BINARY) USING utf8mb4)
WHERE description IS NOT NULL
  AND description != CONVERT(CAST(CONVERT(description USING latin1) AS BINARY) USING utf8mb4);

-- 4. 修复 compositions 表中的 name 字段
UPDATE compositions
SET name = CONVERT(CAST(CONVERT(name USING latin1) AS BINARY) USING utf8mb4)
WHERE name != CONVERT(CAST(CONVERT(name USING latin1) AS BINARY) USING utf8mb4);

-- 5. 修复 processes 表中的 name 字段
UPDATE processes
SET name = CONVERT(CAST(CONVERT(name USING latin1) AS BINARY) USING utf8mb4)
WHERE name != CONVERT(CAST(CONVERT(name USING latin1) AS BINARY) USING utf8mb4);

-- 6. 修复 teams 表中的 team_name 字段
UPDATE teams
SET team_name = CONVERT(CAST(CONVERT(team_name USING latin1) AS BINARY) USING utf8mb4)
WHERE team_name != CONVERT(CAST(CONVERT(team_name USING latin1) AS BINARY) USING utf8mb4);

-- 7. 修复 teams 表中的 description 字段
UPDATE teams
SET description = CONVERT(CAST(CONVERT(description USING latin1) AS BINARY) USING utf8mb4)
WHERE description IS NOT NULL
  AND description != CONVERT(CAST(CONVERT(description USING latin1) AS BINARY) USING utf8mb4);

-- 8. 修复 properties 表中的 property_name 字段
UPDATE properties
SET property_name = CONVERT(CAST(CONVERT(property_name USING latin1) AS BINARY) USING utf8mb4)
WHERE property_name != CONVERT(CAST(CONVERT(property_name USING latin1) AS BINARY) USING utf8mb4);

-- 9. 修复 properties 表中的 notes 字段
UPDATE properties
SET notes = CONVERT(CAST(CONVERT(notes USING latin1) AS BINARY) USING utf8mb4)
WHERE notes IS NOT NULL
  AND notes != CONVERT(CAST(CONVERT(notes USING latin1) AS BINARY) USING utf8mb4);

-- 10. 验证修复结果
SELECT 
    id,
    username,
    full_name,
    status
FROM users 
WHERE username = 'admin';

SELECT 
    id,
    record_number,
    title,
    LEFT(description, 50) as description_preview
FROM experiment_records
LIMIT 5;

SELECT 
    id,
    name
FROM compositions
LIMIT 5;

SELECT 
    id,
    name
FROM processes
LIMIT 5;

SELECT 
    id,
    team_name,
    description
FROM teams
ORDER BY id;

SELECT 
    id,
    property_name,
    property_unit,
    LEFT(notes, 30) as notes_preview
FROM properties
LIMIT 5;

