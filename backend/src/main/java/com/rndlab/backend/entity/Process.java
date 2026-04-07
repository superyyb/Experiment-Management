package com.rndlab.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 工艺实体类
 * 对应数据库processes表
 */
@Data
public class Process {
    private Long id;
    private String name;
    private String processType;
    private BigDecimal temperature;
    private BigDecimal pressure;
    private Integer duration;
    private String description;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

