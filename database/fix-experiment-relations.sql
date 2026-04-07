-- ============================================
-- 修复实验记录的关联关系
-- ============================================
-- 更新实验记录表中的 composition_id 和 process_id
-- 使用方法：docker exec -i rnd-lab-mysql mysql -u root -proot123 rnd_lab_management < fix-experiment-relations.sql

USE rnd_lab_management;

-- 更新实验记录的关联ID
-- EXP-2024-001: 氧化铝 + 高温烧结
UPDATE experiment_records 
SET composition_id = 1, process_id = 1
WHERE record_number = 'EXP-2024-001';

-- EXP-2024-002: 碳化硅 + 热压成型
UPDATE experiment_records 
SET composition_id = 2, process_id = 2
WHERE record_number = 'EXP-2024-002';

-- EXP-2024-003: 氮化硅 + 等静压
UPDATE experiment_records 
SET composition_id = 3, process_id = 3
WHERE record_number = 'EXP-2024-003';

-- EXP-2024-004: 氧化锆 + 高温烧结
UPDATE experiment_records 
SET composition_id = 4, process_id = 1
WHERE record_number = 'EXP-2024-004';

-- 验证修复结果
SELECT 
    er.id,
    er.record_number,
    er.title,
    c.name as composition_name,
    p.name as process_name,
    t.team_name
FROM experiment_records er
LEFT JOIN compositions c ON er.composition_id = c.id
LEFT JOIN processes p ON er.process_id = p.id
LEFT JOIN teams t ON er.team_id = t.id
ORDER BY er.id;


