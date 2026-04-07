package com.rndlab.backend.mapper;

import com.rndlab.backend.entity.Process;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 工艺Mapper接口
 */
@Mapper
public interface ProcessMapper {
    List<Process> findAll();
    Process findById(Long id);
}

