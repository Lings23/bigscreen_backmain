package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 季度报告查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "季度报告查询条件")
public class QuarterlyReportQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "报告季度（格式如2024-Q1）")
    private String reportQuarter;

    @ApiModelProperty(value = "信息安全检索数量-最小")
    @Min(value = 0, message = "信息安全检索数量不能小于0")
    private Integer minSecurityRetrievalCount;

    @ApiModelProperty(value = "信息安全检索数量-最大")
    @Min(value = 0, message = "信息安全检索数量不能小于0")
    private Integer maxSecurityRetrievalCount;

    @ApiModelProperty(value = "信息安全培训数量-最小")
    @Min(value = 0, message = "信息安全培训数量不能小于0")
    private Integer minSecurityTrainingCount;

    @ApiModelProperty(value = "信息安全培训数量-最大")
    @Min(value = 0, message = "信息安全培训数量不能小于0")
    private Integer maxSecurityTrainingCount;

    @ApiModelProperty(value = "信息系统建设整改数量-最小")
    @Min(value = 0, message = "信息系统建设整改数量不能小于0")
    private Integer minSystemRectificationCount;

    @ApiModelProperty(value = "信息系统建设整改数量-最大")
    @Min(value = 0, message = "信息系统建设整改数量不能小于0")
    private Integer maxSystemRectificationCount;

    @ApiModelProperty(value = "创建时间-起始")
    private LocalDateTime createdAtStart;

    @ApiModelProperty(value = "创建时间-结束")
    private LocalDateTime createdAtEnd;
}