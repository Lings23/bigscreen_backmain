package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.ReBaoEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReBaoEventRepository extends JpaRepository<ReBaoEvent, Long> {
}