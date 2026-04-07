#!/bin/bash

# ============================================
# 使用 Docker 检查数据库连接和用户信息
# ============================================

echo "============================================"
echo "检查数据库连接和用户信息（Docker方式）"
echo "============================================"
echo ""

# 检查 Docker 是否运行
if ! docker ps > /dev/null 2>&1; then
    echo "✗ Docker 未运行，请先启动 Docker"
    exit 1
fi

# 检查 MySQL 容器是否运行
MYSQL_CONTAINER=$(docker ps --filter "name=mysql" --format "{{.Names}}" | head -1)
if [ -z "$MYSQL_CONTAINER" ]; then
    echo "✗ MySQL 容器未运行"
    echo "  请执行: docker-compose up -d mysql"
    exit 1
fi

echo "✓ 找到 MySQL 容器: $MYSQL_CONTAINER"
echo ""

# 数据库配置
DB_NAME="rnd_lab_management"
DB_USER="root"
DB_PASS="root123"

echo "1. 测试数据库连接..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" -e "SELECT 1;" 2>&1
if [ $? -eq 0 ]; then
    echo "✓ 数据库连接成功"
else
    echo "✗ 数据库连接失败"
    exit 1
fi

echo ""
echo "2. 检查数据库是否存在..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" -e "SHOW DATABASES LIKE '$DB_NAME';" 2>&1 | grep -q "$DB_NAME"
if [ $? -eq 0 ]; then
    echo "✓ 数据库 $DB_NAME 存在"
else
    echo "✗ 数据库 $DB_NAME 不存在"
    echo "  请先执行 schema.sql 创建数据库"
    exit 1
fi

echo ""
echo "3. 检查 admin 用户..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
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

echo ""
echo "4. 检查 admin 用户的角色..."
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
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
docker exec "$MYSQL_CONTAINER" mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "
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


