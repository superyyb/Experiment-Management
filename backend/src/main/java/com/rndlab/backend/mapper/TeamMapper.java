package com.rndlab.backend.mapper;

import com.rndlab.backend.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * 团队Mapper接口
 */
@Mapper
public interface TeamMapper {
    List<Team> findAll();
    Team findById(Long id);
}

