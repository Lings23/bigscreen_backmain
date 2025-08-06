package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.OutboundIpStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundIpStatRepository extends JpaRepository<OutboundIpStat, Long> {
} 