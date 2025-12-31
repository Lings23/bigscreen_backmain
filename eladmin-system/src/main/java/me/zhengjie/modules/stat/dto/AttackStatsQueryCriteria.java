package me.zhengjie.modules.stat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

/**
 * 攻击统计查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "攻击统计查询条件")
public class AttackStatsQueryCriteria extends BaseQueryCriteria {

    @ApiModelProperty(value = "恶意代码攻击数量-最小")
    @Min(value = 0, message = "恶意代码攻击数量不能小于0")
    private Integer minMaliciousCodeAttack;

    @ApiModelProperty(value = "恶意代码攻击数量-最大")
    @Min(value = 0, message = "恶意代码攻击数量不能小于0")
    private Integer maxMaliciousCodeAttack;

    @ApiModelProperty(value = "漏洞攻击数量-最小")
    @Min(value = 0, message = "漏洞攻击数量不能小于0")
    private Integer minVulnerabilityAttack;

    @ApiModelProperty(value = "漏洞攻击数量-最大")
    @Min(value = 0, message = "漏洞攻击数量不能小于0")
    private Integer maxVulnerabilityAttack;

    @ApiModelProperty(value = "DoS攻击数量-最小")
    @Min(value = 0, message = "DoS攻击数量不能小于0")
    private Integer minDosAttack;

    @ApiModelProperty(value = "DoS攻击数量-最大")
    @Min(value = 0, message = "DoS攻击数量不能小于0")
    private Integer maxDosAttack;

    @ApiModelProperty(value = "扫描探测数量-最小")
    @Min(value = 0, message = "扫描探测数量不能小于0")
    private Integer minScanProbe;

    @ApiModelProperty(value = "扫描探测数量-最大")
    @Min(value = 0, message = "扫描探测数量不能小于0")
    private Integer maxScanProbe;

    @ApiModelProperty(value = "其他攻击数量-最小")
    @Min(value = 0, message = "其他攻击数量不能小于0")
    private Integer minOtherAttack;

    @ApiModelProperty(value = "其他攻击数量-最大")
    @Min(value = 0, message = "其他攻击数量不能小于0")
    private Integer maxOtherAttack;

    @ApiModelProperty(value = "统计日期-起始")
    private LocalDateTime statDateStart;

    @ApiModelProperty(value = "统计日期-结束")
    private LocalDateTime statDateEnd;

    @ApiModelProperty(value = "创建时间-起始")
    private LocalDateTime createdAtStart;

    @ApiModelProperty(value = "创建时间-结束")
    private LocalDateTime createdAtEnd;
}