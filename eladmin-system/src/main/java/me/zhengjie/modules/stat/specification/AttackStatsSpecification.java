package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.AttackStats;
import me.zhengjie.modules.stat.dto.AttackStatsQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 攻击统计查询规格构建器
 */
public class AttackStatsSpecification {

    public static Specification<AttackStats> build(AttackStatsQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索：尝试解析为数字匹配各类攻击数量
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                try {
                    Integer numericValue = Integer.parseInt(criteria.getKeyword().trim());
                    Predicate maliciousPredicate = criteriaBuilder.equal(root.get("maliciousCodeAttack"), numericValue);
                    Predicate vulnerabilityPredicate = criteriaBuilder.equal(root.get("vulnerabilityAttack"), numericValue);
                    Predicate dosPredicate = criteriaBuilder.equal(root.get("dosAttack"), numericValue);
                    Predicate scanPredicate = criteriaBuilder.equal(root.get("scanProbe"), numericValue);
                    Predicate otherPredicate = criteriaBuilder.equal(root.get("otherAttack"), numericValue);
                    predicates.add(criteriaBuilder.or(
                            maliciousPredicate, vulnerabilityPredicate, dosPredicate, scanPredicate, otherPredicate
                    ));
                } catch (NumberFormatException e) {
                    // 若关键字非数字，不匹配任何条件
                }
            }

            // 恶意代码攻击数量范围
            if (criteria.getMinMaliciousCodeAttack() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("maliciousCodeAttack"),
                        criteria.getMinMaliciousCodeAttack()
                ));
            }
            if (criteria.getMaxMaliciousCodeAttack() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("maliciousCodeAttack"),
                        criteria.getMaxMaliciousCodeAttack()
                ));
            }

            // 漏洞攻击数量范围
            if (criteria.getMinVulnerabilityAttack() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("vulnerabilityAttack"),
                        criteria.getMinVulnerabilityAttack()
                ));
            }
            if (criteria.getMaxVulnerabilityAttack() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("vulnerabilityAttack"),
                        criteria.getMaxVulnerabilityAttack()
                ));
            }

            // DoS攻击数量范围
            if (criteria.getMinDosAttack() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("dosAttack"),
                        criteria.getMinDosAttack()
                ));
            }
            if (criteria.getMaxDosAttack() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("dosAttack"),
                        criteria.getMaxDosAttack()
                ));
            }

            // 扫描探测数量范围
            if (criteria.getMinScanProbe() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("scanProbe"),
                        criteria.getMinScanProbe()
                ));
            }
            if (criteria.getMaxScanProbe() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("scanProbe"),
                        criteria.getMaxScanProbe()
                ));
            }

            // 其他攻击数量范围
            if (criteria.getMinOtherAttack() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("otherAttack"),
                        criteria.getMinOtherAttack()
                ));
            }
            if (criteria.getMaxOtherAttack() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("otherAttack"),
                        criteria.getMaxOtherAttack()
                ));
            }

            // 统计日期范围
            if (criteria.getStatDateStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("statDate"),
                        criteria.getStatDateStart()
                ));
            }
            if (criteria.getStatDateEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("statDate"),
                        criteria.getStatDateEnd()
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