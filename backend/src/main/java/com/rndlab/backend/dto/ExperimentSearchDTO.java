package com.rndlab.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 实验记录搜索DTO
 * 用于高级搜索功能：支持过滤、范围、模糊匹配、排序
 */
@Data
public class ExperimentSearchDTO {
    // 模糊匹配字段
    private String keyword; // 搜索关键词（标题、描述）
    private String recordNumber; // 实验编号
    
    // 过滤字段
    private String status; // 状态过滤
    private Long teamId; // 团队过滤
    private Long compositionId; // 成分过滤
    private Long processId; // 工艺过滤
    private Long createdBy; // 创建人过滤
    
    // 范围字段
    private LocalDate startDate; // 实验日期起始
    private LocalDate endDate; // 实验日期结束
    private BigDecimal minTemperature; // 温度范围
    private BigDecimal maxTemperature;
    private BigDecimal minPressure; // 压力范围
    private BigDecimal maxPressure;
    
    // 属性过滤
    private String propertyName; // 属性名称
    private BigDecimal minPropertyValue; // 属性值范围
    private BigDecimal maxPropertyValue;
    private String propertyType; // 属性类型
    
    // 排序字段
    private String sortBy; // 排序字段：createdAt, experimentDate, recordNumber等
    private String sortOrder; // 排序方向：ASC, DESC
    
    // 分页字段
    private Integer page = 1; // 页码，从1开始
    private Integer pageSize = 20; // 每页大小
}

