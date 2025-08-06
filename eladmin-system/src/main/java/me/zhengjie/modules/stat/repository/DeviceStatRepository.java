package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.DeviceStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.time.LocalDateTime;

public interface DeviceStatRepository extends JpaRepository<DeviceStat, Long> {
    List<DeviceStat> findByStatTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Sort sort);
} 