package me.zhengjie.modules.stat.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 资产列表DTO
 * @author system
 * @date 2025-08-01
 */
@Data
public class AssetListDto {

    private Long id;

    @NotBlank(message = "资产IP地址不能为空")
    private String assetIp;

    @NotNull(message = "资产端口不能为空")
    private Integer assetPort;

    private String systemName;

    private String organizationName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
} 