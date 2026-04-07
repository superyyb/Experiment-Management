#!/bin/bash

# ============================================
# 数据库连接和用户检查脚本
# ============================================

echo "============================================"
echo "检查数据库连接和用户信息"
echo "============================================"
echo ""

# 数据库配置（从 application.yml 中获取）
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="rnd_lab_management"
DB_USER="root"
DB_PASS="root123"

echo "1. 测试数据库连接..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" -e "SELECT 1;" 2>&1
if [ $? -eq 0 ]; then
    echo "✓ 数据库连接成功"
else
    echo "✗ 数据库连接失败，请检查："
    echo "  - MySQL 服务是否运行"
    echo "  - 用户名和密码是否正确"
    echo "  - 数据库是否已创建"
    exit 1
fi

echo ""
echo "2. 检查数据库是否存在..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" -e "SHOW DATABASES LIKE '$DB_NAME';" 2>&1 | grep -q "$DB_NAME"
if [ $? -eq 0 ]; then
    echo "✓ 数据库 $DB_NAME 存在"
else
    echo "✗ 数据库 $DB_NAME 不存在，请先执行 schema.sql 创建数据库"
    exit 1
fi

echo ""
echo "3. 检查 admin 用户..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    id,
    username,
    email,
    full_name,
    status,
    CASE WHEN status = 1 THEN '活跃' ELSE '禁用' END as status_text
FROM users 
WHERE username = 'admin';
" 2>&1

if [ $? -eq 0 ]; then
    USER_COUNT=$(mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -N -e "SELECT COUNT(*) FROM users WHERE username = 'admin';" 2>&1)
    if [ "$USER_COUNT" -gt 0 ]; then
        echo "✓ admin 用户存在"
    else
        echo "✗ admin 用户不存在"
        echo "  请执行: mysql -u $DB_USER -p$DB_PASS $DB_NAME < fix-admin-password.sql"
    fi
else
    echo "✗ 查询失败"
fi

echo ""
echo "4. 检查 admin 用户的角色..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    u.username,
    r.role_name,
    r.description
FROM users u
INNER JOIN user_roles ur ON u.id = ur.user_id
INNER JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'admin';
" 2>&1

echo ""
echo "5. 检查所有用户..."
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
SELECT 
    id,
    username,
    email,
    status,
    created_at
FROM users
ORDER BY id;
" 2>&1

echo ""
echo "============================================"
echo "检查完成"
echo "============================================"


