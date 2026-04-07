package com.rndlab.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 提供系统健康状态和根路径访问
 */
@RestController
@RequestMapping
public class HealthController {
    
    /**
     * 根路径健康检查
     * GET /
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "R&D experiment records platform (50,000+ scale) — backend OK");
        response.put("apiBasePath", "/api");
        response.put("endpoints", Map.of(
            "auth", "/api/auth/login",
            "common", "/api/common/options"
        ));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 健康检查端点
     * GET /health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }
}

