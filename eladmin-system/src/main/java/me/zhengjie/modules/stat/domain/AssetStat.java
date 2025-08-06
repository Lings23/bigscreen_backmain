package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asset_stat")
public class AssetStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;
    @Column(name = "network_device", nullable = false)
    private Integer networkDevice;
    @Column(name = "security_device", nullable = false)
    private Integer securityDevice;
    @Column(name = "domain_name", nullable = false)
    private Integer domainName;
    @Column(name = "middleware", nullable = false)
    private Integer middleware;
    @Column(name = "service", nullable = false)
    private Integer service;
    @Column(name = "application", nullable = false)
    private Integer application;
    @Column(name = "website", nullable = false)
    private Integer website;
    @Column(name = "virtual_device", nullable = false)
    private Integer virtualDevice;
    @Column(name = "port", nullable = false)
    private Integer port;
    @Column(name = "host", nullable = false)
    private Integer host;
    @Column(name = "database_count", nullable = false)
    private Integer databaseCount;
    @Column(name = "os_count", nullable = false)
    private Integer osCount;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 