package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.OutboundIpStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface OutboundIpStatRepository extends JpaRepository<OutboundIpStat, Long>, JpaSpecificationExecutor<OutboundIpStat> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<OutboundIpStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据位置关键字搜索
     */
    Page<OutboundIpStat> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    
    /**
     * 根据统计时间范围查询
     */
    Page<OutboundIpStat> findByStatTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据是否国内查询
     */
    Page<OutboundIpStat> findByIsDomestic(Boolean isDomestic, Pageable pageable);
}