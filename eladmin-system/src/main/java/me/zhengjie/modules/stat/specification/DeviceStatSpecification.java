package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.DeviceStat;
import me.zhengjie.modules.stat.dto.DeviceStatQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 设备统计查询规格构建器
 */
public class DeviceStatSpecification {

    public static Specification<DeviceStat> build(DeviceStatQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字搜索 - 尝试解析为数字进行数量匹配
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                try {
                    Integer numericValue = Integer.parseInt(criteria.getKeyword().trim());
                    Predicate onlineCountPredicate = criteriaBuilder.equal(root.get("onlineCount"), numericValue);
                    Predicate offlineCountPredicate = criteriaBuilder.equal(root.get("offlineCount"), numericValue);
                    Predicate alarmCountPredicate = criteriaBuilder.equal(root.get("alarmCount"), numericValue);
                    predicates.add(criteriaBuilder.or(onlineCountPredicate, offlineCountPredicate, alarmCountPredicate));
                } catch (Exception e) {
                    // 如果不是有效数字，忽略关键字搜索
                }
            }

            // 状态
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // 在线数量范围
            if (criteria.getMinOnlineCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("onlineCount"), criteria.getMinOnlineCount()));
            }
            if (criteria.getMaxOnlineCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("onlineCount"), criteria.getMaxOnlineCount()));
            }

            // 离线数量范围
            if (criteria.getMinOfflineCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("offlineCount"), criteria.getMinOfflineCount()));
            }
            if (criteria.getMaxOfflineCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("offlineCount"), criteria.getMaxOfflineCount()));
            }

            // 告警数量范围
            if (criteria.getMinAlarmCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("alarmCount"), criteria.getMinAlarmCount()));
            }
            if (criteria.getMaxAlarmCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("alarmCount"), criteria.getMaxAlarmCount()));
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
