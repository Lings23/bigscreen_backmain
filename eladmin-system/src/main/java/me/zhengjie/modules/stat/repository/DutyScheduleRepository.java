package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.DutySchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DutyScheduleRepository extends JpaRepository<DutySchedule, Long>, JpaSpecificationExecutor<DutySchedule> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<DutySchedule> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 多字段关键字搜索
     */
    Page<DutySchedule> findByOrgNameContainingIgnoreCaseOrLeaderNameContainingIgnoreCaseOrDutyPersonContainingIgnoreCase(
        String orgName, String leaderName, String dutyPerson, Pageable pageable);
    
    /**
     * 根据值班日期范围查询
     */
    Page<DutySchedule> findByDutyDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * 根据部门名称和值班日期查询是否存在记录
     */
    @Query("SELECT COUNT(d) > 0 FROM DutySchedule d WHERE d.orgName = :orgName AND d.dutyDate = :dutyDate")
    boolean existsByOrgNameAndDutyDate(@Param("orgName") String orgName, @Param("dutyDate") LocalDate dutyDate);
    
    /**
     * 根据事件名称和部门名称查询是否存在记录（新的去重逻辑）
     */
    @Query("SELECT COUNT(d) > 0 FROM DutySchedule d WHERE d.eventName = :eventName AND d.orgName = :orgName")
    boolean existsByEventNameAndOrgName(@Param("eventName") String eventName, @Param("orgName") String orgName);
    
    /**
     * 根据事件名称、部门名称和值班日期查询是否存在记录（改进的去重逻辑）
     */
    @Query("SELECT COUNT(d) > 0 FROM DutySchedule d WHERE d.eventName = :eventName AND d.orgName = :orgName AND d.dutyDate = :dutyDate")
    boolean existsByEventNameAndOrgNameAndDutyDate(@Param("eventName") String eventName, @Param("orgName") String orgName, @Param("dutyDate") LocalDate dutyDate);
    
    /**
     * 根据事件名称和部门名称查询记录
     */
    @Query("SELECT d FROM DutySchedule d WHERE d.eventName = :eventName AND d.orgName = :orgName")
    java.util.Optional<DutySchedule> findByEventNameAndOrgName(@Param("eventName") String eventName, @Param("orgName") String orgName);
    
    /**
     * 删除创建时间早于指定时间的记录
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM DutySchedule d WHERE d.createdAt < :createdAt")
    int deleteByCreatedAtBefore(@Param("createdAt") java.time.LocalDateTime createdAt);
} 