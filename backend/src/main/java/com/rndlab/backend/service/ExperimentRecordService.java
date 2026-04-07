package com.rndlab.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rndlab.backend.cache.ExperimentSearchCacheKeyGenerator;
import com.rndlab.backend.dto.ExperimentSearchDTO;
import com.rndlab.backend.dto.PageResult;
import com.rndlab.backend.entity.ExperimentRecord;
import com.rndlab.backend.entity.Property;
import com.rndlab.backend.entity.User;
import com.rndlab.backend.mapper.ExperimentRecordMapper;
import com.rndlab.backend.mapper.PropertyMapper;
import com.rndlab.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Experiment records: MyBatis DAO + Redis-backed Spring Cache (TTL, invalidation on mutations, bounded search keys).
 */
@Service
@RequiredArgsConstructor
public class ExperimentRecordService {

    private static final String SEARCH_CACHE_PREFIX = "experimentSearch:v1:";

    private final ExperimentRecordMapper experimentRecordMapper;
    private final PropertyMapper propertyMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final ExperimentSearchCacheKeyGenerator searchCacheKeyGenerator;

    @Value("${app.cache.search-result-ttl:1800}")
    private long searchResultTtlSeconds;
    
    /**
     * Load by id with cache; {@code sync=true} reduces stampedes on hot ids.
     */
    @Cacheable(value = "experimentRecord", key = "#id", sync = true)
    public ExperimentRecord findById(Long id) {
        ExperimentRecord record = experimentRecordMapper.findById(id);
        if (record != null) {
            // 加载属性列表
            List<Property> properties = propertyMapper.findByExperimentId(id);
            record.setProperties(properties);
        }
        return record;
    }
    
    /**
     * Advanced search with Redis cache (manual JSON + {@link TypeReference} so {@link PageResult} generics round-trip;
     * hashed keys for hot-query safety). TTL matches {@code app.cache.search-result-ttl}.
     */
    public PageResult<ExperimentRecord> search(ExperimentSearchDTO search) {
        String cacheKey = SEARCH_CACHE_PREFIX + searchCacheKeyGenerator.digest(search);
        try {
            String cached = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<PageResult<ExperimentRecord>>() {});
            }
        } catch (Exception e) {
            System.err.println("Redis search cache read failed: " + e.getMessage());
        }

        PageResult<ExperimentRecord> result = loadSearchFromDatabase(search);

        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(result),
                    Duration.ofSeconds(searchResultTtlSeconds));
        } catch (Exception e) {
            System.err.println("Redis search cache write failed: " + e.getMessage());
        }

        return result;
    }

    private PageResult<ExperimentRecord> loadSearchFromDatabase(ExperimentSearchDTO search) {
        try {
            int offset = (search.getPage() - 1) * search.getPageSize();

            List<ExperimentRecord> records = experimentRecordMapper.search(search, offset);

            if (records != null) {
                records.forEach(record -> {
                    if (record != null && record.getId() != null) {
                        List<Property> properties = propertyMapper.findByExperimentId(record.getId());
                        record.setProperties(properties);
                    }
                });
            }

            Long total = experimentRecordMapper.countSearch(search);
            if (total == null) {
                total = 0L;
            }

            return new PageResult<>(records != null ? records : java.util.Collections.emptyList(), total, search.getPage(), search.getPageSize());
        } catch (Exception e) {
            System.err.println("搜索实验记录时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void clearManualSearchCache() {
        Set<String> keys = stringRedisTemplate.keys(SEARCH_CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }
    
    /**
     * Create; evicts detail + search caches after success (write-through style invalidation).
     */
    @Transactional
    @CacheEvict(value = "experimentRecord", allEntries = true)
    public ExperimentRecord create(ExperimentRecord record) {
        // 获取当前登录用户信息
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            // 自动设置创建人ID（如果未设置）
            if (record.getCreatedBy() == null) {
                record.setCreatedBy(currentUser.getId());
            }
            // 自动设置团队ID（如果未设置）
            if (record.getTeamId() == null && currentUser.getTeamId() != null) {
                record.setTeamId(currentUser.getTeamId());
            }
        }
        
        // 生成实验编号（如果未提供）
        if (record.getRecordNumber() == null || record.getRecordNumber().isEmpty()) {
            record.setRecordNumber(generateRecordNumber());
        }
        
        // 插入实验记录
        experimentRecordMapper.insert(record);
        
        // 插入属性
        if (record.getProperties() != null && !record.getProperties().isEmpty()) {
            record.getProperties().forEach(property -> {
                property.setExperimentId(record.getId());
                propertyMapper.insert(property);
            });
        }
        
        ExperimentRecord created = findById(record.getId());
        clearManualSearchCache();
        return created;
    }
    
    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                if (username != null && !username.equals("anonymousUser")) {
                    return userMapper.findByUsername(username);
                }
            }
        } catch (Exception e) {
            System.err.println("获取当前用户失败: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 更新实验记录
     */
    @Transactional
    @CacheEvict(value = "experimentRecord", allEntries = true)
    public ExperimentRecord update(Long id, ExperimentRecord record) {
        record.setId(id);
        experimentRecordMapper.update(record);
        
        // 更新属性：先删除再插入
        propertyMapper.deleteByExperimentId(id);
        if (record.getProperties() != null && !record.getProperties().isEmpty()) {
            record.getProperties().forEach(property -> {
                property.setExperimentId(id);
                propertyMapper.insert(property);
            });
        }
        
        ExperimentRecord updated = findById(id);
        clearManualSearchCache();
        return updated;
    }
    
    /**
     * 删除实验记录
     */
    @Transactional
    @CacheEvict(value = "experimentRecord", allEntries = true)
    public void delete(Long id) {
        // 删除属性（外键级联删除，但显式删除更清晰）
        propertyMapper.deleteByExperimentId(id);
        // 删除实验记录
        experimentRecordMapper.delete(id);
        clearManualSearchCache();
    }
    
    /**
     * 生成实验编号
     * 格式：EXP-YYYY-XXX
     */
    private String generateRecordNumber() {
        String year = String.valueOf(java.time.Year.now().getValue());
        // 查询当年最大编号
        // 简化实现，实际应该查询数据库
        return "EXP-" + year + "-" + String.format("%03d", 
            (int)(Math.random() * 1000));
    }
}

