package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AttackStats;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface AttackStatsRepository extends JpaRepository<AttackStats, Long> {
    // 按统计日期查询（避免同一日期重复统计）
    Optional<AttackStats> findByStatDate(LocalDateTime statDate);
}