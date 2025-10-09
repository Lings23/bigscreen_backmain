package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.AbnormalEventStat;
import me.zhengjie.modules.stat.dto.AbnormalEventStatQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public interface AbnormalEventStatService extends BaseStatService<AbnormalEventStat> {
    
    /**
     * 标准CRUD查询 - 支持动态Specification和Pageable
     */
    Page<AbnormalEventStat> findAll(Specification<AbnormalEventStat> spec, Pageable pageable);
    
    /**
     * 分页查询所有数据
     */
    Page<AbnormalEventStat> findAll(Integer page, Integer size);
    
    /**
     * 根据时间范围查询
     */
    Page<AbnormalEventStat> findByTimePeriod(LocalDate startDate, LocalDate endDate, Integer page, Integer size);
    
    /**
     * 根据关键字搜索
     */
    Page<AbnormalEventStat> findByKeyword(String keyword, Integer page, Integer size);
    
    /**
     * 复杂条件查询
     */
    Page<AbnormalEventStat> findByCriteria(AbnormalEventStatQueryCriteria criteria, Pageable pageable);
    
    /**
     * 创建分页对象
     */
    Pageable createPageable(Integer page, Integer size);
    
}