package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.RiskScore;
import me.zhengjie.modules.stat.dto.RiskScoreQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public interface RiskScoreService extends BaseStatService<RiskScore> {
    
    /**
     * 根据Specification查询分页数据
     */
    Page<RiskScore> findAll(Specification<RiskScore> spec, Pageable pageable);
    
    /**
     * 根据时间范围查询分页数据
     */
    Page<RiskScore> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);
    
    /**
     * 根据关键字查询分页数据
     */
    Page<RiskScore> findByKeyword(String keyword, Integer page, Integer size);
    
    /**
     * 根据查询条件查询分页数据
     */
    Page<RiskScore> findByCriteria(RiskScoreQueryCriteria criteria, Pageable pageable);
    
    /**
     * 创建分页对象
     */
    Pageable createPageable(Integer page, Integer size);
}