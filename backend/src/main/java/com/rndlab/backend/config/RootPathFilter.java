package com.rndlab.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 根路径过滤器
 * 处理根路径请求，重定向到 /api/ 或返回 JSON 响应
 * 
 * 由于 application.yml 中设置了 context-path: /api
 * 所有 Spring Boot 控制器都在 /api 路径下
 * 这个过滤器在 Spring Security 之前处理根路径请求
 */
@Component
@Order(1)
public class RootPathFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        
        // 如果是根路径，返回 JSON 响应
        if ("/".equals(path)) {
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.getWriter().write(
                "{\n" +
                "  \"status\": \"UP\",\n" +
                "  \"message\": \"R&D experiment records platform (50,000+ scale) — backend OK\",\n" +
                "  \"apiBasePath\": \"/api\",\n" +
                "  \"endpoints\": {\n" +
                "    \"root\": \"/api/\",\n" +
                "    \"auth\": \"/api/auth/login\",\n" +
                "    \"common\": \"/api/common/options\",\n" +
                "    \"health\": \"/api/health\"\n" +
                "  }\n" +
                "}"
            );
            httpResponse.getWriter().flush();
            return;
        }
        
        // 继续处理其他请求
        chain.doFilter(request, response);
    }
}


