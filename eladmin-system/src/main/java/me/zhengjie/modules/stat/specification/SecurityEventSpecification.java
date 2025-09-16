package me.zhengjie.modules.stat.specification;

import me.zhengjie.modules.stat.domain.SecurityEvent;
import me.zhengjie.modules.stat.dto.SecurityEventQueryCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 安全事件查询规格构建器
 * @author system
 * @date 2025-09-08
 */
public class SecurityEventSpecification {
    
    /**
     * 根据查询条件构建Specification
     * @param criteria 查询条件
     * @return Specification对象
     */
    public static Specification<SecurityEvent> createSpecification(SecurityEventQueryCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 时间范围查询（创建时间）
            if (criteria.getCreatedAtStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtStart()));
            }
            
            if (criteria.getCreatedAtEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), criteria.getCreatedAtEnd()));
            }
            
            // 事件时间范围查询
            if (criteria.getEventTimeStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("eventTime"), criteria.getEventTimeStart()));
            }
            
            if (criteria.getEventTimeEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("eventTime"), criteria.getEventTimeEnd()));
            }
            
            // 系统名称查询
            if (criteria.getSystemName() != null && !criteria.getSystemName().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("systemName")), 
                    "%" + criteria.getSystemName().toLowerCase() + "%"
                ));
            }
            
            // IP地址查询
            if (criteria.getIpAddress() != null && !criteria.getIpAddress().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("ipAddress"), "%" + criteria.getIpAddress() + "%"));
            }
            
            // 状态查询
            if (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }
            
            // 来源查询
            if (criteria.getSource() != null && !criteria.getSource().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("source")), 
                    "%" + criteria.getSource().toLowerCase() + "%"
                ));
            }
            
            // 内容关键字查询
            if (criteria.getContentKeyword() != null && !criteria.getContentKeyword().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("content")), 
                    "%" + criteria.getContentKeyword().toLowerCase() + "%"
                ));
            }
            
            // 通用关键字查询（搜索系统名称、IP地址、来源）
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String keyword = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate systemNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("systemName")), keyword
                );
                Predicate ipPredicate = criteriaBuilder.like(root.get("ipAddress"), keyword);
                Predicate sourcePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("source")), keyword
                );
                
                predicates.add(criteriaBuilder.or(systemNamePredicate, ipPredicate, sourcePredicate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
