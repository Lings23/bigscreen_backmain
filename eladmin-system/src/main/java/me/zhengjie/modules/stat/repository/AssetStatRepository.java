package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AssetStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetStatRepository extends JpaRepository<AssetStat, Long> {
} 