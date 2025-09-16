package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 告警统计查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "告警统计查询条件")
public class AlarmCountStatQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "统计日期开始")
    private LocalDate statDateStart;

    @ApiModelProperty(value = "统计日期结束")
    private LocalDate statDateEnd;

    @ApiModelProperty(value = "最小成功数量")
    @Min(value = 0, message = "成功数量不能小于0")
    private Integer minSuccessCount;

    @ApiModelProperty(value = "最大成功数量")
    @Min(value = 0, message = "成功数量不能小于0")
    private Integer maxSuccessCount;

    @ApiModelProperty(value = "最小可疑数量")
    @Min(value = 0, message = "可疑数量不能小于0")
    private Integer minSuspiciousCount;

    @ApiModelProperty(value = "最大可疑数量")
    @Min(value = 0, message = "可疑数量不能小于0")
    private Integer maxSuspiciousCount;

    @ApiModelProperty(value = "最小尝试数量")
    @Min(value = 0, message = "尝试数量不能小于0")
    private Integer minAttemptCount;

    @ApiModelProperty(value = "最大尝试数量")
    @Min(value = 0, message = "尝试数量不能小于0")
    private Integer maxAttemptCount;
}
