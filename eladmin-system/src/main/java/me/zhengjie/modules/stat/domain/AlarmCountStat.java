package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alarm_count_stat")
public class AlarmCountStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;
    @Column(name = "success_count", nullable = false)
    private Integer successCount;
    @Column(name = "suspicious_count", nullable = false)
    private Integer suspiciousCount;
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 