package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.OutboundIpStat;
import me.zhengjie.modules.stat.dto.OutboundIpStatQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 出站IP统计查询规格构建器
 */
public class OutboundIpStatSpecification {

    public static Specification<OutboundIpStat> build(OutboundIpStatQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索 - 在位置中搜索
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().trim() + "%";
                Predicate locationPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("location")), 
                    keyword.toLowerCase()
                );
                predicates.add(locationPredicate);
            }

            // 位置
            if (criteria.getLocation() != null && !criteria.getLocation().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("location")), 
                    "%" + criteria.getLocation().toLowerCase() + "%"
                ));
            }

            // 是否国内
            if (criteria.getIsDomestic() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isDomestic"), criteria.getIsDomestic()));
            }

            // 状态
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // IP数量范围
            if (criteria.getIpCountMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("ipCount"), criteria.getIpCountMin()));
            }
            if (criteria.getIpCountMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("ipCount"), criteria.getIpCountMax()));
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
