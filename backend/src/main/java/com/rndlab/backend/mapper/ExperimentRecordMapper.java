package com.rndlab.backend.mapper;

import com.rndlab.backend.entity.ExperimentRecord;
import com.rndlab.backend.dto.ExperimentSearchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 实验记录Mapper接口
 * 负责实验记录的数据库操作
 */
@Mapper
public interface ExperimentRecordMapper {
    
    /**
     * 根据ID查询实验记录
     */
    ExperimentRecord findById(Long id);
    
    /**
     * 高级搜索实验记录
     * 支持过滤、范围、模糊匹配、排序
     */
    List<ExperimentRecord> search(@Param("search") ExperimentSearchDTO search, @Param("offset") int offset);
    
    /**
     * 统计搜索结果总数
     */
    Long countSearch(@Param("search") ExperimentSearchDTO search);
    
    /**
     * 插入实验记录
     */
    int insert(ExperimentRecord record);
    
    /**
     * 更新实验记录
     */
    int update(ExperimentRecord record);
    
    /**
     * 删除实验记录
     */
    int delete(Long id);
    
    /**
     * 根据实验编号查询
     */
    ExperimentRecord findByRecordNumber(String recordNumber);
}

