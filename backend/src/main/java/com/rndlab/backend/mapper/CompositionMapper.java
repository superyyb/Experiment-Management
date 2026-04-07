package com.rndlab.backend.mapper;

import com.rndlab.backend.entity.Composition;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 成分Mapper接口
 */
@Mapper
public interface CompositionMapper {
    List<Composition> findAll();
    Composition findById(Long id);
}

