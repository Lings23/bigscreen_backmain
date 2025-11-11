package me.zhengjie.modules.stat.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "quarterly_report")
public class QuarterlyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_quarter", nullable = false)
    private String reportQuarter; // 报告季度，格式如"2024-Q1"

    @Column(name = "security_retrieval_count", nullable = false)
    private Integer securityRetrievalCount; // 信息安全检索数量

    @Column(name = "security_training_count", nullable = false)
    private Integer securityTrainingCount; // 信息安全培训数量

    @Column(name = "system_rectification_count", nullable = false)
    private Integer systemRectificationCount; // 信息系统建设整改数量

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间
}