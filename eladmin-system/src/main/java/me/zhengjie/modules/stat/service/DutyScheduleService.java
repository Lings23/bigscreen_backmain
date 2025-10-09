package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.DutySchedule;
import me.zhengjie.modules.stat.dto.DutyScheduleQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public interface DutyScheduleService extends BaseStatService<DutySchedule> {
    
    /**
     * 根据Specification查询分页数据
     */
    Page<DutySchedule> findAll(Specification<DutySchedule> spec, Pageable pageable);
    
    /**
     * 根据时间范围查询分页数据
     */
    Page<DutySchedule> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);
    
    /**
     * 根据关键字查询分页数据
     */
    Page<DutySchedule> findByKeyword(String keyword, Integer page, Integer size);
    
    /**
     * 根据查询条件查询分页数据
     */
    Page<DutySchedule> findByCriteria(DutyScheduleQueryCriteria criteria, Pageable pageable);
    
    /**
     * 创建分页对象
     */
    Pageable createPageable(Integer page, Integer size);
}