package com.rndlab.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * 对应数据库users表
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Long teamId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联数据
    private Team team;
    private List<Role> roles;
}

