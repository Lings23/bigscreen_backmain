package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.AttackFlyline;
import me.zhengjie.modules.stat.dto.AttackFlylineQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 攻击飞线查询规格构建器
 */
public class AttackFlylineSpecification {

    public static Specification<AttackFlyline> build(AttackFlylineQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索 - 在源IP、目标IP、攻击方法中搜索
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().trim() + "%";
                Predicate sourceIpPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("sourceIp")), 
                    keyword.toLowerCase()
                );
                Predicate targetIpPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("targetIp")), 
                    keyword.toLowerCase()
                );
                Predicate attackMethodPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("attackMethod")), 
                    keyword.toLowerCase()
                );
                predicates.add(criteriaBuilder.or(sourceIpPredicate, targetIpPredicate, attackMethodPredicate));
            }

            // 源IP地址
            if (criteria.getSourceIp() != null && !criteria.getSourceIp().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("sourceIp")), 
                    "%" + criteria.getSourceIp().toLowerCase() + "%"
                ));
            }

            // 目标IP地址
            if (criteria.getTargetIp() != null && !criteria.getTargetIp().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("targetIp")), 
                    "%" + criteria.getTargetIp().toLowerCase() + "%"
                ));
            }

            // 源位置名称
            if (criteria.getSourceLocationName() != null && !criteria.getSourceLocationName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("sourceLocationName")), 
                    "%" + criteria.getSourceLocationName().toLowerCase() + "%"
                ));
            }

            // 目标位置名称
            if (criteria.getTargetLocationName() != null && !criteria.getTargetLocationName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("targetLocationName")), 
                    "%" + criteria.getTargetLocationName().toLowerCase() + "%"
                ));
            }

            // 攻击方法
            if (criteria.getAttackMethod() != null && !criteria.getAttackMethod().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("attackMethod"), criteria.getAttackMethod()));
            }

            // 目标系统
            if (criteria.getTargetSystem() != null && !criteria.getTargetSystem().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("targetSystem"), criteria.getTargetSystem()));
            }

            // 来源是否国内
            if (criteria.getSourceIsDomestic() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sourceIsDomestic"), criteria.getSourceIsDomestic()));
            }

            // 状态
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // 攻击时间范围
            if (criteria.getAttackTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("attackTime"), criteria.getAttackTimeStart()));
            }
            if (criteria.getAttackTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("attackTime"), criteria.getAttackTimeEnd()));
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
