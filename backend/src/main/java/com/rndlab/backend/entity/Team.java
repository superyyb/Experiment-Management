package com.rndlab.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队实体类
 * 对应数据库teams表
 */
@Data
public class Team {
    private Long id;
    private String teamName;
    private String description;
    private LocalDateTime createdAt;
}

