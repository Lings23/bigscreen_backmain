package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "outbound_ip_stat")
public class OutboundIpStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "is_domestic", nullable = false)
    private Boolean isDomestic;

    @Column(name = "stat_time", nullable = false)
    private LocalDateTime statTime;

    @Column(name = "ip_count", nullable = false)
    private Integer ipCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 