package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AttackFlyline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

/**
 * 攻击飞线数据访问层
 */
public interface AttackFlylineRepository extends JpaRepository<AttackFlyline, Long>, JpaSpecificationExecutor<AttackFlyline> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<AttackFlyline> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据攻击时间范围查询
     */
    Page<AttackFlyline> findByAttackTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 多字段关键字搜索
     */
    Page<AttackFlyline> findBySourceIpContainingIgnoreCaseOrTargetIpContainingIgnoreCaseOrAttackMethodContainingIgnoreCase(
        String sourceIp, String targetIp, String attackMethod, Pageable pageable);
    
    /**
     * 根据攻击方法查询
     */
    Page<AttackFlyline> findByAttackMethod(String attackMethod, Pageable pageable);
    
    /**
     * 根据目标系统查询
     */
    Page<AttackFlyline> findByTargetSystem(String targetSystem, Pageable pageable);
    
    /**
     * 根据来源是否国内查询
     */
    Page<AttackFlyline> findBySourceIsDomestic(Boolean sourceIsDomestic, Pageable pageable);
}