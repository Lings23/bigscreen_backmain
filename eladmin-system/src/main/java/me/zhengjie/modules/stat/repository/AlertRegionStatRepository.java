package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AlertRegionStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRegionStatRepository extends JpaRepository<AlertRegionStat, Long> {
} 