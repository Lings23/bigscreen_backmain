package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attack_flyline")
public class AttackFlyline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_ip", nullable = false)
    private String sourceIp;
    @Column(name = "target_ip", nullable = false)
    private String targetIp;
    @Column(name = "source_location_name")
    private String sourceLocationName;
    @Column(name = "source_lng", nullable = false, precision = 10, scale = 6)
    private BigDecimal sourceLng;
    @Column(name = "source_lat", nullable = false, precision = 10, scale = 6)
    private BigDecimal sourceLat;
    @Column(name = "target_location_name")
    private String targetLocationName;
    @Column(name = "target_lng", nullable = false, precision = 10, scale = 6)
    private BigDecimal targetLng;
    @Column(name = "target_lat", nullable = false, precision = 10, scale = 6)
    private BigDecimal targetLat;
    @Column(name = "attack_method", nullable = false)
    private String attackMethod;
    @Column(name = "target_system", nullable = false)
    private String targetSystem;
    @Column(name = "attack_time", nullable = false)
    private LocalDateTime attackTime;
    @Column(name = "source_is_domestic", nullable = false)
    private Boolean sourceIsDomestic;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 