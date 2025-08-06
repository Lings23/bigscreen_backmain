package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AlarmCountStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmCountStatRepository extends JpaRepository<AlarmCountStat, Long> {
} 