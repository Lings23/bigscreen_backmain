package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.AlarmCountStat;
import me.zhengjie.modules.stat.dto.AlarmCountStatQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 告警统计查询规格构建器
 */
public class AlarmCountStatSpecification {

    public static Specification<AlarmCountStat> build(AlarmCountStatQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索 - 尝试解析为日期
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                try {
                    java.time.LocalDate date = java.time.LocalDate.parse(criteria.getKeyword().trim());
                    predicates.add(criteriaBuilder.equal(root.get("statDate"), date));
                } catch (Exception e) {
                    // 如果不是有效日期格式，忽略关键字搜索
                }
            }

            // 状态
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // 统计日期范围
            if (criteria.getStatDateStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("statDate"), criteria.getStatDateStart()));
            }
            if (criteria.getStatDateEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("statDate"), criteria.getStatDateEnd()));
            }

            // 成功数量范围
            if (criteria.getMinSuccessCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("successCount"), criteria.getMinSuccessCount()));
            }
            if (criteria.getMaxSuccessCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("successCount"), criteria.getMaxSuccessCount()));
            }

            // 可疑数量范围
            if (criteria.getMinSuspiciousCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("suspiciousCount"), criteria.getMinSuspiciousCount()));
            }
            if (criteria.getMaxSuspiciousCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("suspiciousCount"), criteria.getMaxSuspiciousCount()));
            }

            // 尝试数量范围
            if (criteria.getMinAttemptCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("attemptCount"), criteria.getMinAttemptCount()));
            }
            if (criteria.getMaxAttemptCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("attemptCount"), criteria.getMaxAttemptCount()));
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
