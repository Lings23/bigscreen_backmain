package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 风险评分查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "风险评分查询条件")
public class RiskScoreQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "系统名称")
    private String systemName;

    @ApiModelProperty(value = "风险评分最小值")
    private BigDecimal riskScoreMin;

    @ApiModelProperty(value = "风险评分最大值")
    private BigDecimal riskScoreMax;

    @ApiModelProperty(value = "评分日期开始")
    private LocalDate scoreDateStart;

    @ApiModelProperty(value = "评分日期结束")
    private LocalDate scoreDateEnd;
}
