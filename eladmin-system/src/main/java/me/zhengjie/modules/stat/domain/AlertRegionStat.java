package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alert_region_stat")
public class AlertRegionStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "region_name", nullable = false)
    private String regionName;
    @Column(name = "region_type", nullable = false)
    private String regionType;
    @Column(name = "alert_count", nullable = false)
    private Integer alertCount;
    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
} 