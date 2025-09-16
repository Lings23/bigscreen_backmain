package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.AlertRegionStat;
import me.zhengjie.modules.stat.dto.AlertRegionStatQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 区域告警统计查询规格构建器
 */
public class AlertRegionStatSpecification {

    public static Specification<AlertRegionStat> build(AlertRegionStatQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索 - 在区域名称中搜索
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().trim() + "%";
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("regionName")), 
                    keyword.toLowerCase()
                ));
            }

            // 区域名称精确匹配
            if (criteria.getRegionName() != null && !criteria.getRegionName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("regionName"), criteria.getRegionName()));
            }

            // 区域类型
            if (criteria.getRegionType() != null && !criteria.getRegionType().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("regionType"), criteria.getRegionType()));
            }

            // 状态
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // 告警数量范围
            if (criteria.getMinAlertCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("alertCount"), criteria.getMinAlertCount()));
            }
            if (criteria.getMaxAlertCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("alertCount"), criteria.getMaxAlertCount()));
            }

            // 统计日期范围
            if (criteria.getStatDateStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("statDate"), criteria.getStatDateStart()));
            }
            if (criteria.getStatDateEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("statDate"), criteria.getStatDateEnd()));
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
