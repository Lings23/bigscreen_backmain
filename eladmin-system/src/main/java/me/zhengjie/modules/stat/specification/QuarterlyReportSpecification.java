package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.QuarterlyReport;
import me.zhengjie.modules.stat.dto.QuarterlyReportQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 季度报告查询规格构建器
 */
public class QuarterlyReportSpecification {

    public static Specification<QuarterlyReport> build(QuarterlyReportQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索：模糊匹配季度或数字匹配各类数量
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = criteria.getKeyword().trim();
                try {
                    // 尝试解析为数字，匹配各类数量字段
                    Integer numericValue = Integer.parseInt(keyword);
                    Predicate retrievalPredicate = criteriaBuilder.equal(root.get("securityRetrievalCount"), numericValue);
                    Predicate trainingPredicate = criteriaBuilder.equal(root.get("securityTrainingCount"), numericValue);
                    Predicate rectificationPredicate = criteriaBuilder.equal(root.get("systemRectificationCount"), numericValue);
                    predicates.add(criteriaBuilder.or(retrievalPredicate, trainingPredicate, rectificationPredicate));
                } catch (NumberFormatException e) {
                    // 非数字则模糊匹配季度（如"2024-Q1"）
                    predicates.add(criteriaBuilder.like(root.get("reportQuarter"), "%" + keyword + "%"));
                }
            }

            // 报告季度精确匹配
            if (criteria.getReportQuarter() != null && !criteria.getReportQuarter().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("reportQuarter"),
                        criteria.getReportQuarter().trim()
                ));
            }

            // 信息安全检索数量范围
            if (criteria.getMinSecurityRetrievalCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("securityRetrievalCount"),
                        criteria.getMinSecurityRetrievalCount()
                ));
            }
            if (criteria.getMaxSecurityRetrievalCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("securityRetrievalCount"),
                        criteria.getMaxSecurityRetrievalCount()
                ));
            }

            // 信息安全培训数量范围
            if (criteria.getMinSecurityTrainingCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("securityTrainingCount"),
                        criteria.getMinSecurityTrainingCount()
                ));
            }
            if (criteria.getMaxSecurityTrainingCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("securityTrainingCount"),
                        criteria.getMaxSecurityTrainingCount()
                ));
            }

            // 信息系统建设整改数量范围
            if (criteria.getMinSystemRectificationCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("systemRectificationCount"),
                        criteria.getMinSystemRectificationCount()
                ));
            }
            if (criteria.getMaxSystemRectificationCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("systemRectificationCount"),
                        criteria.getMaxSystemRectificationCount()
                ));
            }

            // 创建时间范围
            if (criteria.getCreatedAtStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        criteria.getCreatedAtStart()
                ));
            }
            if (criteria.getCreatedAtEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        criteria.getCreatedAtEnd()
                ));
            }

            // 组合所有条件
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}