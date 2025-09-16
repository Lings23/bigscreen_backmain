package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.AssetStat;
import me.zhengjie.modules.stat.dto.AssetStatQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产统计数据查询规格构建器
 * @author system
 * @date 2025-09-08
 */
public class AssetStatSpecification {
    
    /**
     * 根据查询条件构建Specification
     * @param criteria 查询条件
     * @return Specification对象
     */
    public static Specification<AssetStat> createSpecification(AssetStatQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 时间范围查询
            if (criteria.getStartDate() != null) {
                LocalDateTime startDateTime = criteria.getStartDate().atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDateTime));
            }
            
            if (criteria.getEndDate() != null) {
                LocalDateTime endDateTime = criteria.getEndDate().atTime(23, 59, 59);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDateTime));
            }
            
            // 统计日期查询
            if (criteria.getStatDate() != null && !criteria.getStatDate().trim().isEmpty()) {
                try {
                    predicates.add(criteriaBuilder.equal(root.get("statDate"), 
                        java.time.LocalDate.parse(criteria.getStatDate())));
                } catch (Exception e) {
                    // 忽略无效的日期格式
                }
            }
            
            // 网络设备数量范围
            if (criteria.getNetworkDeviceMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("networkDevice"), criteria.getNetworkDeviceMin()));
            }
            
            if (criteria.getNetworkDeviceMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("networkDevice"), criteria.getNetworkDeviceMax()));
            }
            
            // 安全设备数量范围
            if (criteria.getSecurityDeviceMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("securityDevice"), criteria.getSecurityDeviceMin()));
            }
            
            if (criteria.getSecurityDeviceMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("securityDevice"), criteria.getSecurityDeviceMax()));
            }
            
            // 状态查询
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                // AssetStat没有status字段，这里作为扩展预留
            }
            
            // 关键字搜索（可以扩展为搜索多个字段）
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                // 由于AssetStat主要是数值字段，关键字搜索可以预留给其他字段
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
