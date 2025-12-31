package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.ReBaoEvent;
import me.zhengjie.modules.stat.dto.ReBaoEventQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 重保事件查询规格构建器
 */
public class ReBaoEventSpecification {

    public static Specification<ReBaoEvent> build(ReBaoEventQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索：模糊匹配事件名称、参与单位
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = criteria.getKeyword().trim();
                Predicate namePredicate = criteriaBuilder.like(
                        root.get("eventName"), "%" + keyword + "%"
                );
                Predicate unitPredicate = criteriaBuilder.like(
                        root.get("participatingUnits"), "%" + keyword + "%"
                );
                predicates.add(criteriaBuilder.or(namePredicate, unitPredicate));
            }

            // 事件名称精确匹配
            if (criteria.getEventName() != null && !criteria.getEventName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("eventName"),
                        criteria.getEventName().trim()
                ));
            }

            // 参与单位精确匹配
            if (criteria.getParticipatingUnits() != null && !criteria.getParticipatingUnits().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("participatingUnits"),
                        criteria.getParticipatingUnits().trim()
                ));
            }

            // 事件开始时间范围
            if (criteria.getStartTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startTime"),
                        criteria.getStartTimeStart()
                ));
            }
            if (criteria.getStartTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("startTime"),
                        criteria.getStartTimeEnd()
                ));
            }

            // 事件结束时间范围
            if (criteria.getEndTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("endTime"),
                        criteria.getEndTimeStart()
                ));
            }
            if (criteria.getEndTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("endTime"),
                        criteria.getEndTimeEnd()
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