package com.rndlab.backend.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 实验记录实体类
 * 对应数据库experiment_records表
 * 这是系统的核心实体，关联composition、process和properties
 */
@Data
public class ExperimentRecord {
    private Long id;
    private String recordNumber;
    private String title;
    private String description;
    private Long compositionId;
    private Long processId;
    private LocalDate experimentDate;
    private String status; // DRAFT, COMPLETED, ARCHIVED
    private Long teamId;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 关联数据
    private Composition composition;
    private Process process;
    private Team team;
    private User creator;
    private List<Property> properties;
    private List<Composition> compositions; // 多成分关联
}

