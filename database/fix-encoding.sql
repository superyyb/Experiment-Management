-- ============================================
-- 修复数据库字符集编码问题
-- ============================================
-- 如果数据出现乱码，请执行此脚本来检查和修复字符集设置

USE rnd_lab_management;

-- 1. 检查当前字符集设置
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';

-- 2. 检查数据库字符集
SELECT 
    SCHEMA_NAME,
    DEFAULT_CHARACTER_SET_NAME,
    DEFAULT_COLLATION_NAME
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'rnd_lab_management';

-- 3. 检查表的字符集
SELECT 
    TABLE_NAME,
    TABLE_COLLATION
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'rnd_lab_management';

-- 4. 检查列的字符集
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CHARACTER_SET_NAME,
    COLLATION_NAME
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'rnd_lab_management'
  AND CHARACTER_SET_NAME IS NOT NULL;

-- 5. 如果需要修复，执行以下命令（谨慎操作，建议先备份数据）
-- 修改数据库字符集
-- ALTER DATABASE rnd_lab_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 修改表的字符集（示例，根据实际表名调整）
-- ALTER TABLE experiment_records CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE compositions CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE processes CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;


