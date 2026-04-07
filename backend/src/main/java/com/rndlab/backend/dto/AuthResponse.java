package com.rndlab.backend.dto;

import lombok.Data;
import java.util.List;

/**
 * 认证响应DTO
 * 包含JWT token和用户信息
 */
@Data
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String fullName;
    private Long teamId;
    private List<String> roles;
    
    public AuthResponse(String token, String username, String email, String fullName, Long teamId, List<String> roles) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.teamId = teamId;
        this.roles = roles;
    }
}

