package com.rndlab.backend.security;

import com.rndlab.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 * 在每个请求中验证JWT token并设置认证信息
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        // 从请求头中获取token
        String token = getTokenFromRequest(request);
        logger.info("🔍 JWT过滤器 - 请求路径: {}, Token存在: {}", request.getRequestURI(), StringUtils.hasText(token));
        
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            try {
                // 从token中获取用户名和角色
                String username = jwtUtil.getUsernameFromToken(token);
                List<String> roles = jwtUtil.getRolesFromToken(token);
                
                logger.info("🔐 JWT验证 - 用户名: {}, 角色: {}", username, roles);
                
                // 处理roles为null或空的情况
                if (roles != null && !roles.isEmpty()) {
                    // 创建认证对象，添加ROLE_前缀（Spring Security要求）
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                        .filter(role -> role != null && !role.trim().isEmpty())
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .collect(Collectors.toList());
                    
                    logger.info("🔐 JWT验证 - 转换后的权限: {}", authorities);
                    
                    if (!authorities.isEmpty()) {
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                username, null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 设置到Security上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("✅ JWT验证成功 - 已设置认证信息: {}", username);
                    } else {
                        logger.warn("⚠️ JWT验证 - 用户 {} 没有有效角色", username);
                    }
                } else {
                    logger.warn("⚠️ JWT验证 - 用户 {} 的角色列表为空或null", username);
                }
            } catch (Exception e) {
                // Token解析失败，继续处理（让Security处理未认证请求）
                logger.error("JWT token解析失败: " + e.getMessage(), e);
            }
        } else {
            if (!StringUtils.hasText(token)) {
                logger.warn("⚠️ JWT验证 - 请求中未找到token: {}", request.getRequestURI());
            } else {
                logger.warn("⚠️ JWT验证 - token验证失败: {}", request.getRequestURI());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求头中提取JWT token
     * 格式：Authorization: Bearer <token>
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

