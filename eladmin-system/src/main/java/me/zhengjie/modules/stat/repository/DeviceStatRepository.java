package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.DeviceStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.time.LocalDateTime;

public interface DeviceStatRepository extends JpaRepository<DeviceStat, Long>, JpaSpecificationExecutor<DeviceStat> {
    
    List<DeviceStat> findByStatTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Sort sort);
    
    /**
     * 根据创建时间范围查询
     */
    Page<DeviceStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据统计时间范围查询
     */
    Page<DeviceStat> findByStatTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据设备数量查询
     */
    Page<DeviceStat> findByOnlineCountOrOfflineCountOrAlarmCount(Integer onlineCount, Integer offlineCount, Integer alarmCount, Pageable pageable);
}