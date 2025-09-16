package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.DutySchedule;
import me.zhengjie.modules.stat.dto.DutyScheduleQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 值班安排查询规格构建器
 */
public class DutyScheduleSpecification {

    public static Specification<DutySchedule> build(DutyScheduleQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索 - 在组织名称、负责人姓名、值班人员中搜索
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().trim() + "%";
                Predicate orgNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("orgName")), 
                    keyword.toLowerCase()
                );
                Predicate leaderNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("leaderName")), 
                    keyword.toLowerCase()
                );
                Predicate dutyPersonPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("dutyPerson")), 
                    keyword.toLowerCase()
                );
                predicates.add(criteriaBuilder.or(orgNamePredicate, leaderNamePredicate, dutyPersonPredicate));
            }

            // 组织名称
            if (criteria.getOrgName() != null && !criteria.getOrgName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("orgName")), 
                    "%" + criteria.getOrgName().toLowerCase() + "%"
                ));
            }

            // 负责人姓名
            if (criteria.getLeaderName() != null && !criteria.getLeaderName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("leaderName")), 
                    "%" + criteria.getLeaderName().toLowerCase() + "%"
                ));
            }

            // 负责人电话
            if (criteria.getLeaderPhone() != null && !criteria.getLeaderPhone().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("leaderPhone"), 
                    "%" + criteria.getLeaderPhone() + "%"
                ));
            }

            // 值班人员
            if (criteria.getDutyPerson() != null && !criteria.getDutyPerson().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("dutyPerson")), 
                    "%" + criteria.getDutyPerson().toLowerCase() + "%"
                ));
            }

            // 值班电话
            if (criteria.getDutyPhone() != null && !criteria.getDutyPhone().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    root.get("dutyPhone"), 
                    "%" + criteria.getDutyPhone() + "%"
                ));
            }

            // 事件名称
            if (criteria.getEventName() != null && !criteria.getEventName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("eventName")), 
                    "%" + criteria.getEventName().toLowerCase() + "%"
                ));
            }

            // 状态
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // 值班日期范围
            if (criteria.getDutyDateStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dutyDate"), criteria.getDutyDateStart()));
            }
            if (criteria.getDutyDateEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dutyDate"), criteria.getDutyDateEnd()));
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
