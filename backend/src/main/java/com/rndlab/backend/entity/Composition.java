package com.rndlab.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 成分实体类
 * 对应数据库compositions表
 */
@Data
public class Composition {
    private Long id;
    private String name;
    private String formula;
    private String category;
    private String description;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

