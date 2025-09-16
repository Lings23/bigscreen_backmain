package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.SecurityEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long>, JpaSpecificationExecutor<SecurityEvent> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<SecurityEvent> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据事件时间范围查询
     */
    Page<SecurityEvent> findByEventTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据系统名称模糊查询
     */
    Page<SecurityEvent> findBySystemNameContainingIgnoreCase(String systemName, Pageable pageable);
    
    /**
     * 根据IP地址查询
     */
    Page<SecurityEvent> findByIpAddressContaining(String ipAddress, Pageable pageable);
    
    /**
     * 根据状态查询
     */
    Page<SecurityEvent> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据来源查询
     */
    Page<SecurityEvent> findBySourceContainingIgnoreCase(String source, Pageable pageable);
    
    /**
     * 根据内容关键字查询
     */
    Page<SecurityEvent> findByContentContainingIgnoreCase(String content, Pageable pageable);
    
    /**
     * 综合查询
     */
    @Query("SELECT s FROM SecurityEvent s WHERE " +
           "(:systemName IS NULL OR LOWER(s.systemName) LIKE LOWER(CONCAT('%', :systemName, '%'))) AND " +
           "(:ipAddress IS NULL OR s.ipAddress LIKE CONCAT('%', :ipAddress, '%')) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:source IS NULL OR LOWER(s.source) LIKE LOWER(CONCAT('%', :source, '%'))) AND " +
           "(:eventTimeStart IS NULL OR s.eventTime >= :eventTimeStart) AND " +
           "(:eventTimeEnd IS NULL OR s.eventTime <= :eventTimeEnd) AND " +
           "(:contentKeyword IS NULL OR LOWER(s.content) LIKE LOWER(CONCAT('%', :contentKeyword, '%')))")
    Page<SecurityEvent> findByCriteria(@Param("systemName") String systemName,
                                       @Param("ipAddress") String ipAddress,
                                       @Param("status") String status,
                                       @Param("source") String source,
                                       @Param("eventTimeStart") LocalDateTime eventTimeStart,
                                       @Param("eventTimeEnd") LocalDateTime eventTimeEnd,
                                       @Param("contentKeyword") String contentKeyword,
                                       Pageable pageable);
}