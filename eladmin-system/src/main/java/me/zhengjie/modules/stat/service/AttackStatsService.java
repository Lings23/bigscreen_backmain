package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.AttackStats;
import java.time.LocalDateTime;
import java.util.Optional;

public interface AttackStatsService extends BaseStatService<AttackStats> {
    // 扩展按统计日期查询的方法
    Optional<AttackStats> findByStatDate(LocalDateTime statDate);
}