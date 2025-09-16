package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AbnormalEventStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface AbnormalEventStatRepository extends JpaRepository<AbnormalEventStat, Long>, JpaSpecificationExecutor<AbnormalEventStat> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<AbnormalEventStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据统计时间范围查询
     */
    Page<AbnormalEventStat> findByStatTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}