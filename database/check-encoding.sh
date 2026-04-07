#!/bin/bash

# ============================================
# 检查数据库字符编码和乱码问题
# ============================================

echo "============================================"
echo "检查数据库字符编码和乱码问题"
echo "============================================"
echo ""

# 数据库配置
DB_NAME="rnd_lab_management"
DB_USER="root"
DB_PASS="root123"
MYSQL_CONTAINER="rnd-lab-mysql"

echo "1. 检查数据库字符集..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" -e "
SELECT 
    SCHEMA_NAME,
    DEFAULT_CHARACTER_SET_NAME as '字符集',
    DEFAULT_COLLATION_NAME as '排序规则'
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = '$DB_NAME';
" 2>&1

echo ""
echo "2. 检查表的字符集..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    TABLE_NAME as '表名',
    TABLE_COLLATION as '排序规则'
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = '$DB_NAME'
  AND TABLE_NAME IN ('users', 'experiment_records', 'compositions', 'processes')
ORDER BY TABLE_NAME;
" 2>&1

echo ""
echo "3. 检查用户表字段的字符集..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    TABLE_NAME as '表名',
    COLUMN_NAME as '字段名',
    CHARACTER_SET_NAME as '字符集',
    COLLATION_NAME as '排序规则',
    COLUMN_TYPE as '字段类型'
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = '$DB_NAME'
  AND TABLE_NAME = 'users'
  AND CHARACTER_SET_NAME IS NOT NULL
ORDER BY COLUMN_NAME;
" 2>&1

echo ""
echo "4. 检查 admin 用户的原始数据（十六进制）..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    id,
    username,
    email,
    HEX(full_name) as 'full_name_hex',
    full_name as 'full_name_text',
    status
FROM users 
WHERE username = 'admin';
" 2>&1

echo ""
echo "5. 检查实验记录表的字符集..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    TABLE_NAME as '表名',
    COLUMN_NAME as '字段名',
    CHARACTER_SET_NAME as '字符集',
    COLLATION_NAME as '排序规则'
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = '$DB_NAME'
  AND TABLE_NAME = 'experiment_records'
  AND COLUMN_NAME IN ('title', 'description')
  AND CHARACTER_SET_NAME IS NOT NULL;
" 2>&1

echo ""
echo "6. 检查实验记录数据的原始值（十六进制）..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    id,
    record_number,
    HEX(title) as 'title_hex',
    title as 'title_text',
    HEX(description) as 'description_hex',
    LEFT(description, 50) as 'description_text'
FROM experiment_records
LIMIT 3;
" 2>&1

echo ""
echo "7. 检查成分表数据的原始值..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    id,
    HEX(name) as 'name_hex',
    name as 'name_text'
FROM compositions
LIMIT 3;
" 2>&1

echo ""
echo "8. 检查工艺表数据的原始值..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    id,
    HEX(name) as 'name_hex',
    name as 'name_text'
FROM processes
LIMIT 3;
" 2>&1

echo ""
echo "9. 检查 MySQL 连接字符集..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';
" 2>&1

echo ""
echo "============================================"
echo "检查完成"
echo "============================================"
echo ""
echo "说明："
echo "- 如果字符集不是 utf8mb4，需要修复"
echo "- 如果 HEX 值显示异常，说明数据本身已损坏"
echo "- 如果字符集正确但显示乱码，可能是应用层编码问题"


