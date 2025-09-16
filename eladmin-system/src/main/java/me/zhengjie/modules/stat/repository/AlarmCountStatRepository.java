package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AlarmCountStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AlarmCountStatRepository extends JpaRepository<AlarmCountStat, Long>, JpaSpecificationExecutor<AlarmCountStat> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<AlarmCountStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据统计日期查询
     */
    Page<AlarmCountStat> findByStatDate(LocalDate statDate, Pageable pageable);
    
    /**
     * 根据统计日期范围查询
     */
    Page<AlarmCountStat> findByStatDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
} 