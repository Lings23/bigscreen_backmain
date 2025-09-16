package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 区域告警统计查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "区域告警统计查询条件")
public class AlertRegionStatQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "区域名称")
    private String regionName;

    @ApiModelProperty(value = "区域类型")
    private String regionType;

    @ApiModelProperty(value = "统计日期开始")
    private LocalDate statDateStart;

    @ApiModelProperty(value = "统计日期结束")
    private LocalDate statDateEnd;

    @ApiModelProperty(value = "最小告警数量")
    @Min(value = 0, message = "告警数量不能小于0")
    private Integer minAlertCount;

    @ApiModelProperty(value = "最大告警数量")
    @Min(value = 0, message = "告警数量不能小于0")
    private Integer maxAlertCount;
}
