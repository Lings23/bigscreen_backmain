package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 值班安排查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "值班安排查询条件")
public class DutyScheduleQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "组织名称")
    private String orgName;

    @ApiModelProperty(value = "负责人姓名")
    private String leaderName;

    @ApiModelProperty(value = "负责人电话")
    private String leaderPhone;

    @ApiModelProperty(value = "值班人员")
    private String dutyPerson;

    @ApiModelProperty(value = "值班电话")
    private String dutyPhone;

    @ApiModelProperty(value = "值班日期开始")
    private LocalDate dutyDateStart;

    @ApiModelProperty(value = "值班日期结束")
    private LocalDate dutyDateEnd;

    @ApiModelProperty(value = "事件名称")
    private String eventName;
}
