package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.RiskScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskScoreRepository extends JpaRepository<RiskScore, Long> {
} 