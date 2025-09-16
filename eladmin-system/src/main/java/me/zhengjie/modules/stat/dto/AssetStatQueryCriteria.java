package me.zhengjie.modules.stat.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资产统计查询条件
 * @author system
 * @date 2025-09-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetStatQueryCriteria extends BaseQueryCriteria {
    
    /**
     * 统计日期（精确匹配）
     */
    private String statDate;
    
    /**
     * 网络设备数量范围 - 最小值
     */
    private Integer networkDeviceMin;
    
    /**
     * 网络设备数量范围 - 最大值
     */
    private Integer networkDeviceMax;
    
    /**
     * 安全设备数量范围 - 最小值
     */
    private Integer securityDeviceMin;
    
    /**
     * 安全设备数量范围 - 最大值
     */
    private Integer securityDeviceMax;
}
