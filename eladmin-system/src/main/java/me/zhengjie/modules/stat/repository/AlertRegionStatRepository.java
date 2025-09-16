package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AlertRegionStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AlertRegionStatRepository extends JpaRepository<AlertRegionStat, Long>, JpaSpecificationExecutor<AlertRegionStat> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<AlertRegionStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据区域名称模糊查询
     */
    Page<AlertRegionStat> findByRegionNameContainingIgnoreCase(String regionName, Pageable pageable);
    
    /**
     * 根据区域类型查询
     */
    Page<AlertRegionStat> findByRegionType(String regionType, Pageable pageable);
    
    /**
     * 根据统计日期查询
     */
    Page<AlertRegionStat> findByStatDate(LocalDate statDate, Pageable pageable);
    
    /**
     * 根据统计日期范围查询
     */
    Page<AlertRegionStat> findByStatDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}