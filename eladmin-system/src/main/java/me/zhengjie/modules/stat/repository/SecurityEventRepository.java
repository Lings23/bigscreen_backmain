package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.SecurityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
} 