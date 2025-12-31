package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 上报事件查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "上报事件查询条件")
public class ReBaoEventQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "事件名称")
    private String eventName;

    @ApiModelProperty(value = "参与单位")
    private String participatingUnits;

    @ApiModelProperty(value = "事件开始时间-起始")
    private LocalDateTime startTimeStart;

    @ApiModelProperty(value = "事件开始时间-结束")
    private LocalDateTime startTimeEnd;

    @ApiModelProperty(value = "事件结束时间-起始")
    private LocalDateTime endTimeStart;

    @ApiModelProperty(value = "事件结束时间-结束")
    private LocalDateTime endTimeEnd;

    @ApiModelProperty(value = "创建时间-起始")
    private LocalDateTime createdAtStart;

    @ApiModelProperty(value = "创建时间-结束")
    private LocalDateTime createdAtEnd;
}