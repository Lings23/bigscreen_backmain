package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "abnormal_event_stat")
public class AbnormalEventStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_time", nullable = false)
    private LocalDateTime statTime;

    @Column(name = "outbound", nullable = false)
    private Integer outbound;

    @Column(name = "outside_to_inside", nullable = false)
    private Integer outsideToInside;

    @Column(name = "lateral_move", nullable = false)
    private Integer lateralMove;

    @Column(name = "other", nullable = false)
    private Integer other;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 