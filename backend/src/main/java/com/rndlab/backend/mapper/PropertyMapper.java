package com.rndlab.backend.mapper;

import com.rndlab.backend.entity.Property;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 属性Mapper接口
 */
@Mapper
public interface PropertyMapper {
    List<Property> findByExperimentId(Long experimentId);
    int insert(Property property);
    int deleteByExperimentId(Long experimentId);
}

