package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.AbnormalEventStat;
import me.zhengjie.modules.stat.dto.AbnormalEventStatQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 异常事件统计查询规格构建器
 */
public class AbnormalEventStatSpecification {

    public static Specification<AbnormalEventStat> build(AbnormalEventStatQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索 - 在事件类型中搜索
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().trim() + "%";
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("eventType")), 
                    keyword.toLowerCase()
                ));
            }

            // 事件类型精确匹配
            if (criteria.getEventType() != null && !criteria.getEventType().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("eventType"), criteria.getEventType()));
            }

            // 严重程度
            if (criteria.getSeverity() != null && !criteria.getSeverity().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("severity"), criteria.getSeverity()));
            }

            // 状态
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // 事件数量范围
            if (criteria.getMinEventCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventCount"), criteria.getMinEventCount()));
            }
            if (criteria.getMaxEventCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventCount"), criteria.getMaxEventCount()));
            }

            // 统计时间范围
            if (criteria.getStatTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("statTime"), criteria.getStatTimeStart()));
            }
            if (criteria.getStatTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("statTime"), criteria.getStatTimeEnd()));
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
