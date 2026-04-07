package com.rndlab.backend.controller;

import com.rndlab.backend.entity.Composition;
import com.rndlab.backend.entity.Process;
import com.rndlab.backend.entity.Team;
import com.rndlab.backend.mapper.CompositionMapper;
import com.rndlab.backend.mapper.ProcessMapper;
import com.rndlab.backend.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用控制器
 * 提供下拉选项数据（成分、工艺、团队等）
 */
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
    
    private final CompositionMapper compositionMapper;
    private final ProcessMapper processMapper;
    private final TeamMapper teamMapper;
    
    /**
     * 获取所有下拉选项数据
     */
    @GetMapping("/options")
    public ResponseEntity<Map<String, List<?>>> getOptions() {
        Map<String, List<?>> options = new HashMap<>();
        options.put("compositions", compositionMapper.findAll());
        options.put("processes", processMapper.findAll());
        options.put("teams", teamMapper.findAll());
        return ResponseEntity.ok(options);
    }
    
    /**
     * 临时测试端点：生成admin123的BCrypt密码
     * TODO: 使用后删除此端点
     */
    @GetMapping("/test/bcrypt")
    public ResponseEntity<Map<String, String>> generateBCrypt() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hashedPassword = encoder.encode(password);
        
        Map<String, String> result = new HashMap<>();
        result.put("plaintext", password);
        result.put("bcrypt_hash", hashedPassword);
        result.put("note", "更新数据库: UPDATE users SET password = ? WHERE username = 'admin'");
        
        return ResponseEntity.ok(result);
    }
}

