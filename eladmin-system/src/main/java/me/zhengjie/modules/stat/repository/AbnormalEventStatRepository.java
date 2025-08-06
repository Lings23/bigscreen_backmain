package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AbnormalEventStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbnormalEventStatRepository extends JpaRepository<AbnormalEventStat, Long> {
} 