package com.rndlab.backend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 属性实体类
 * 对应数据库properties表
 */
@Data
public class Property {
    private Long id;
    private Long experimentId;
    private String propertyName;
    private BigDecimal propertyValue;
    private String propertyUnit;
    private String propertyType;
    private String notes;
    private LocalDateTime createdAt;
}

