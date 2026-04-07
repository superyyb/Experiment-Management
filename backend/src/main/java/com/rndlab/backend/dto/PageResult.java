package com.rndlab.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 分页结果封装类
 * 用于返回分页查询结果
 */
@Data
@NoArgsConstructor
public class PageResult<T> {
    private List<T> data; // 数据列表
    private Long total; // 总记录数
    private Integer page; // 当前页码
    private Integer pageSize; // 每页大小
    private Integer totalPages; // 总页数
    
    public PageResult(List<T> data, Long total, Integer page, Integer pageSize) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }
}

