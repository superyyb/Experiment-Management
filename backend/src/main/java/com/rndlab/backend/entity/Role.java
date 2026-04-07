package com.rndlab.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 角色实体类
 * 对应数据库roles表
 */
@Data
public class Role {
    private Long id;
    private String roleName;
    private String description;
    private LocalDateTime createdAt;
}

