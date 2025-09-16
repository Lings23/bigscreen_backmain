package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 异常事件统计查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "异常事件统计查询条件")
public class AbnormalEventStatQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "事件类型")
    private String eventType;

    @ApiModelProperty(value = "严重程度")
    private String severity;

    @ApiModelProperty(value = "最小事件数量")
    @Min(value = 0, message = "事件数量不能小于0")
    private Integer minEventCount;

    @ApiModelProperty(value = "最大事件数量")
    @Min(value = 0, message = "事件数量不能小于0")
    private Integer maxEventCount;

    @ApiModelProperty(value = "统计时间开始")
    private LocalDateTime statTimeStart;

    @ApiModelProperty(value = "统计时间结束")
    private LocalDateTime statTimeEnd;
}
