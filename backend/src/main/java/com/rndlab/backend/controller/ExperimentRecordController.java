package com.rndlab.backend.controller;

import com.rndlab.backend.dto.ExperimentSearchDTO;
import com.rndlab.backend.dto.PageResult;
import com.rndlab.backend.entity.ExperimentRecord;
import com.rndlab.backend.service.ExperimentRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 实验记录控制器
 * 提供实验记录的CRUD和高级搜索功能
 */
@RestController
@RequestMapping("/experiments")
@RequiredArgsConstructor
public class ExperimentRecordController {
    
    private final ExperimentRecordService experimentRecordService;
    
    /**
     * 高级搜索实验记录
     * GET /api/experiments/search
     * 支持过滤、范围、模糊匹配、排序
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER', 'VIEWER', 'TEAM_LEADER')")
    public ResponseEntity<PageResult<ExperimentRecord>> search(
            @ModelAttribute ExperimentSearchDTO search) {
        // 设置默认值
        if (search.getPage() == null || search.getPage() < 1) {
            search.setPage(1);
        }
        if (search.getPageSize() == null || search.getPageSize() < 1) {
            search.setPageSize(20);
        }
        
        PageResult<ExperimentRecord> result = experimentRecordService.search(search);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据ID查询实验记录
     * GET /api/experiments/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER', 'VIEWER', 'TEAM_LEADER')")
    public ResponseEntity<ExperimentRecord> findById(@PathVariable Long id) {
        ExperimentRecord record = experimentRecordService.findById(id);
        return ResponseEntity.ok(record);
    }
    
    /**
     * 创建实验记录
     * POST /api/experiments
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER', 'TEAM_LEADER')")
    public ResponseEntity<ExperimentRecord> create(@RequestBody ExperimentRecord record) {
        ExperimentRecord created = experimentRecordService.create(record);
        return ResponseEntity.ok(created);
    }
    
    /**
     * 更新实验记录
     * PUT /api/experiments/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESEARCHER', 'TEAM_LEADER')")
    public ResponseEntity<ExperimentRecord> update(
            @PathVariable Long id,
            @RequestBody ExperimentRecord record) {
        ExperimentRecord updated = experimentRecordService.update(id, record);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 删除实验记录
     * DELETE /api/experiments/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_LEADER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        experimentRecordService.delete(id);
        return ResponseEntity.ok().build();
    }
}

