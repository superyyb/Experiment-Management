package com.rndlab.backend.service;

import com.rndlab.backend.dto.AuthResponse;
import com.rndlab.backend.dto.LoginRequest;
import com.rndlab.backend.entity.User;
import com.rndlab.backend.mapper.UserMapper;
import com.rndlab.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务类
 * 处理用户登录和JWT token生成
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * 用户登录（只读事务：保证 MyBatis 懒加载 roles 在同一 SqlSession 内完成，JWT 含 RBAC 角色）
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // 查询用户
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 生成JWT token
        List<String> roles;
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            roles = user.getRoles().stream()
                .filter(role -> role != null && role.getRoleName() != null)
                .map(role -> role.getRoleName())
                .collect(Collectors.toList());
        } else {
            // 如果没有角色，返回空列表
            roles = Collections.emptyList();
        }
        
        // 添加日志以便调试
        System.out.println("登录用户: " + user.getUsername() + ", 角色列表: " + roles);
        
        String token = jwtUtil.generateToken(user.getUsername(), roles);
        
        return new AuthResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getTeamId(),
            roles
        );
    }
}

