package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.AttackFlyline;
import me.zhengjie.modules.stat.dto.AttackFlylineQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public interface AttackFlylineService extends BaseStatService<AttackFlyline> {
    
    /**
     * 根据Specification查询分页数据
     */
    Page<AttackFlyline> findAll(Specification<AttackFlyline> spec, Pageable pageable);
    
    /**
     * 根据时间范围查询分页数据
     */
    Page<AttackFlyline> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);
    
    /**
     * 根据关键字查询分页数据
     */
    Page<AttackFlyline> findByKeyword(String keyword, Integer page, Integer size);
    
    /**
     * 根据查询条件查询分页数据
     */
    Page<AttackFlyline> findByCriteria(AttackFlylineQueryCriteria criteria, Pageable pageable);
    
    /**
     * 创建分页对象
     */
    Pageable createPageable(Integer page, Integer size);
}