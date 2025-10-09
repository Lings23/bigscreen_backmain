package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.SecurityEvent;
import me.zhengjie.modules.stat.dto.SecurityEventQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public interface SecurityEventService extends BaseStatService<SecurityEvent> {
    
    /**
     * 标准CRUD查询 - 支持动态Specification和Pageable
     */
    Page<SecurityEvent> findAll(Specification<SecurityEvent> spec, Pageable pageable);
    
    /**
     * 分页查询所有数据
     */
    Page<SecurityEvent> findAll(Integer page, Integer size);
    
    /**
     * 根据时间范围查询
     */
    Page<SecurityEvent> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);
    
    /**
     * 根据关键字搜索
     */
    Page<SecurityEvent> findByKeyword(String keyword, Integer page, Integer size);
    
    /**
     * 复杂条件查询
     */
    Page<SecurityEvent> findByCriteria(SecurityEventQueryCriteria criteria, Pageable pageable);
    
    /**
     * 创建分页对象
     */
    Pageable createPageable(Integer page, Integer size);
}