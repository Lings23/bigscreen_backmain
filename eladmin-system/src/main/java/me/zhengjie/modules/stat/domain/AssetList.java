package me.zhengjie.modules.stat.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 资产列表实体类
 * @author system
 * @date 2025-08-01
 */
@Data
@Entity
@Table(name = "asset_list", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"asset_ip", "asset_port"}, name = "uniq_ip_port")
})
public class AssetList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "资产IP地址不能为空")
    @Column(name = "asset_ip", nullable = false, length = 45)
    private String assetIp;

    @NotNull(message = "资产端口不能为空")
    @Column(name = "asset_port", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer assetPort;

    @Column(name = "system_name", length = 100)
    private String systemName;

    @Column(name = "organization_name", length = 100)
    private String organizationName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
} 