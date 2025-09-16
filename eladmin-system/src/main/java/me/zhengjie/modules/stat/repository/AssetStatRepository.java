package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AssetStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface AssetStatRepository extends JpaRepository<AssetStat, Long>, JpaSpecificationExecutor<AssetStat> {
    
    /**
     * 根据创建时间范围查询
     */
    Page<AssetStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据统计日期范围查询
     */
    Page<AssetStat> findByStatDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    /**
     * 根据统计日期查询
     */
    Page<AssetStat> findByStatDate(LocalDate statDate, Pageable pageable);
    
    /**
     * 根据网络设备数量范围查询
     */
    @Query("SELECT a FROM AssetStat a WHERE a.networkDevice BETWEEN :min AND :max")
    Page<AssetStat> findByNetworkDeviceBetween(@Param("min") Integer min, @Param("max") Integer max, Pageable pageable);
    
    /**
     * 根据安全设备数量范围查询
     */
    @Query("SELECT a FROM AssetStat a WHERE a.securityDevice BETWEEN :min AND :max")
    Page<AssetStat> findBySecurityDeviceBetween(@Param("min") Integer min, @Param("max") Integer max, Pageable pageable);
    
    /**
     * 综合查询 - 按日期范围和设备数量范围
     */
    @Query("SELECT a FROM AssetStat a WHERE " +
           "(:startDate IS NULL OR a.statDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.statDate <= :endDate) AND " +
           "(:networkDeviceMin IS NULL OR a.networkDevice >= :networkDeviceMin) AND " +
           "(:networkDeviceMax IS NULL OR a.networkDevice <= :networkDeviceMax)")
    Page<AssetStat> findByCriteria(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("networkDeviceMin") Integer networkDeviceMin,
                                   @Param("networkDeviceMax") Integer networkDeviceMax,
                                   Pageable pageable);
}