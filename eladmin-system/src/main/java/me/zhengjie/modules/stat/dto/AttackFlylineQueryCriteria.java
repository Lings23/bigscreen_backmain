package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 攻击飞线查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "攻击飞线查询条件")
public class AttackFlylineQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "源IP地址")
    private String sourceIp;

    @ApiModelProperty(value = "目标IP地址")
    private String targetIp;

    @ApiModelProperty(value = "源位置名称")
    private String sourceLocationName;

    @ApiModelProperty(value = "目标位置名称")
    private String targetLocationName;

    @ApiModelProperty(value = "攻击方法")
    private String attackMethod;

    @ApiModelProperty(value = "目标系统")
    private String targetSystem;

    @ApiModelProperty(value = "攻击时间开始")
    private LocalDateTime attackTimeStart;

    @ApiModelProperty(value = "攻击时间结束")
    private LocalDateTime attackTimeEnd;

    @ApiModelProperty(value = "来源是否国内")
    private Boolean sourceIsDomestic;
}
