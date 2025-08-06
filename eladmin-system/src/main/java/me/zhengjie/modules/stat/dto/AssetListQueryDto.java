package me.zhengjie.modules.stat.dto;

import lombok.Data;

/**
 * 资产列表查询条件DTO
 * @author system
 * @date 2025-08-01
 */
@Data
public class AssetListQueryDto {

    /**
     * 资产IP地址（模糊查询）
     */
    private String assetIp;

    /**
     * 系统名称（模糊查询）
     */
    private String systemName;

    /**
     * 所属单位（模糊查询）
     */
    private String organizationName;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    /**
     * 排序字段
     */
    private String sortBy = "createdAt";

    /**
     * 排序方向（asc/desc）
     */
    private String sortDirection = "desc";
} 