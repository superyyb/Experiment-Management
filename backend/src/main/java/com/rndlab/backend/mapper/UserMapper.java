package com.rndlab.backend.mapper;

import com.rndlab.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    User findByUsername(String username);
    User findById(Long id);
}

