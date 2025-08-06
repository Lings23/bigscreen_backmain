package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_stat")
public class DeviceStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_time", nullable = false)
    private LocalDateTime statTime;

    @Column(name = "online_count", nullable = false)
    private Integer onlineCount;

    @Column(name = "offline_count", nullable = false)
    private Integer offlineCount;

    @Column(name = "alarm_count", nullable = false)
    private Integer alarmCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 