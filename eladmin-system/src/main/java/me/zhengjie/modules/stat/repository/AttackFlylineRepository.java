package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AttackFlyline;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 攻击飞线数据访问层
 */
public interface AttackFlylineRepository extends JpaRepository<AttackFlyline, Long> {
    // 不要添加任何查询方法
} 