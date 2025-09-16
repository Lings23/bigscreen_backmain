package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.RiskScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface RiskScoreRepository extends JpaRepository<RiskScore, Long>, JpaSpecificationExecutor<RiskScore> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<RiskScore> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据系统名称关键字搜索
     */
    Page<RiskScore> findBySystemNameContainingIgnoreCase(String systemName, Pageable pageable);
    
    /**
     * 根据评分日期范围查询
     */
    Page<RiskScore> findByScoreDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * 根据风险评分范围查询
     */
    Page<RiskScore> findByRiskScoreBetween(BigDecimal minScore, BigDecimal maxScore, Pageable pageable);
}