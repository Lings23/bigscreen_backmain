package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.AlertRegionStat;
import me.zhengjie.modules.stat.dto.AlertRegionStatQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public interface AlertRegionStatService extends BaseStatService<AlertRegionStat> {
    
    /**
     * 根据Specification查询分页数据
     */
    Page<AlertRegionStat> findAll(Specification<AlertRegionStat> spec, Pageable pageable);
    
    /**
     * 根据时间范围查询分页数据
     */
    Page<AlertRegionStat> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);
    
    /**
     * 根据关键字查询分页数据
     */
    Page<AlertRegionStat> findByKeyword(String keyword, Integer page, Integer size);
    
    /**
     * 根据查询条件查询分页数据
     */
    Page<AlertRegionStat> findByCriteria(AlertRegionStatQueryCriteria criteria, Pageable pageable);
    
    /**
     * 创建分页对象
     */
    Pageable createPageable(Integer page, Integer size);
}