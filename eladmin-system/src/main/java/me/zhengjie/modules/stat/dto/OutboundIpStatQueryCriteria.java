package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 出站IP统计查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "出站IP统计查询条件")
public class OutboundIpStatQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "是否国内")
    private Boolean isDomestic;

    @ApiModelProperty(value = "统计时间开始")
    private LocalDateTime statTimeStart;

    @ApiModelProperty(value = "统计时间结束")
    private LocalDateTime statTimeEnd;

    @ApiModelProperty(value = "IP数量最小值")
    private Integer ipCountMin;

    @ApiModelProperty(value = "IP数量最大值")
    private Integer ipCountMax;
}
