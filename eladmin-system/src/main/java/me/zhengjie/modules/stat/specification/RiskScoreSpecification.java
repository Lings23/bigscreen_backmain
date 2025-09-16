package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.RiskScore;
import me.zhengjie.modules.stat.dto.RiskScoreQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 风险评分查询规格构建器
 */
public class RiskScoreSpecification {

    public static Specification<RiskScore> build(RiskScoreQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索 - 在系统名称中搜索
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().trim() + "%";
                Predicate systemNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("systemName")), 
                    keyword.toLowerCase()
                );
                predicates.add(systemNamePredicate);
            }

            // 系统名称
            if (criteria.getSystemName() != null && !criteria.getSystemName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("systemName")), 
                    "%" + criteria.getSystemName().toLowerCase() + "%"
                ));
            }

            // 状态
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // 风险评分范围
            if (criteria.getRiskScoreMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("riskScore"), criteria.getRiskScoreMin()));
            }
            if (criteria.getRiskScoreMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("riskScore"), criteria.getRiskScoreMax()));
            }

            // 评分日期范围
            if (criteria.getScoreDateStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("scoreDate"), criteria.getScoreDateStart()));
            }
            if (criteria.getScoreDateEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("scoreDate"), criteria.getScoreDateEnd()));
            }

            // 创建时间范围
            if (criteria.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"), 
                    criteria.getStartDate().atStartOfDay()
                ));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"), 
                    criteria.getEndDate().atTime(23, 59, 59)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
