-- ============================================
-- 修复成分和工艺表的乱码数据
-- ============================================
-- 由于数据已完全损坏（显示为 ???），需要重新插入正确的数据
-- 使用方法：docker exec -i rnd-lab-mysql mysql -u root -proot123 rnd_lab_management < fix-compositions-processes.sql

USE rnd_lab_management;

-- 1. 清空并重新插入成分数据
DELETE FROM experiment_compositions;
DELETE FROM compositions;

INSERT INTO compositions (id, name, formula, category, description, created_by) VALUES
(1, '氧化铝', 'Al2O3', '氧化物', '高纯度氧化铝粉末', 1),
(2, '碳化硅', 'SiC', '碳化物', '碳化硅陶瓷材料', 1),
(3, '氮化硅', 'Si3N4', '氮化物', '氮化硅陶瓷材料', 1),
(4, '氧化锆', 'ZrO2', '氧化物', '氧化锆稳定陶瓷', 2);

-- 2. 清空并重新插入工艺数据
DELETE FROM processes;

INSERT INTO processes (id, name, process_type, temperature, pressure, duration, description, created_by) VALUES
(1, '高温烧结', 'SINTERING', 1600.00, 101325.00, 120, '高温烧结工艺，温度1600℃，持续2小时', 1),
(2, '热压成型', 'HOT_PRESSING', 1500.00, 20000000.00, 60, '热压成型工艺，温度1500℃，压力20MPa', 1),
(3, '等静压', 'CIP', 25.00, 300000000.00, 30, '冷等静压工艺，室温，压力300MPa', 2);

-- 3. 重新关联实验记录和成分
INSERT INTO experiment_compositions (experiment_id, composition_id, ratio) VALUES
(1, 1, 100.0000),
(2, 2, 100.0000),
(3, 3, 100.0000),
(4, 4, 100.0000);

-- 4. 验证修复结果
SELECT 
    id,
    name,
    formula,
    category
FROM compositions
ORDER BY id;

SELECT 
    id,
    name,
    process_type,
    temperature
FROM processes
ORDER BY id;


