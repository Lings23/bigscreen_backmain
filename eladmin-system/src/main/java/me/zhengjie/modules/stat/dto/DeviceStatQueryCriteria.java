package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 设备统计查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "设备统计查询条件")
public class DeviceStatQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "统计时间开始")
    private LocalDateTime statTimeStart;

    @ApiModelProperty(value = "统计时间结束")
    private LocalDateTime statTimeEnd;

    @ApiModelProperty(value = "最小在线数量")
    @Min(value = 0, message = "在线数量不能小于0")
    private Integer minOnlineCount;

    @ApiModelProperty(value = "最大在线数量")
    @Min(value = 0, message = "在线数量不能小于0")
    private Integer maxOnlineCount;

    @ApiModelProperty(value = "最小离线数量")
    @Min(value = 0, message = "离线数量不能小于0")
    private Integer minOfflineCount;

    @ApiModelProperty(value = "最大离线数量")
    @Min(value = 0, message = "离线数量不能小于0")
    private Integer maxOfflineCount;

    @ApiModelProperty(value = "最小告警数量")
    @Min(value = 0, message = "告警数量不能小于0")
    private Integer minAlarmCount;

    @ApiModelProperty(value = "最大告警数量")
    @Min(value = 0, message = "告警数量不能小于0")
    private Integer maxAlarmCount;
}
