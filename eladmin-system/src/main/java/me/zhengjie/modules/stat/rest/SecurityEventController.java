package me.zhengjie.modules.stat.rest;

import io.swagger.annotations.Api;
import me.zhengjie.annotation.rest.AnonymousAccess;
import me.zhengjie.modules.stat.domain.SecurityEvent;
import me.zhengjie.modules.stat.service.SecurityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Api(tags = "安全事件统计数据查询接口")
@RestController
@RequestMapping("/api/stat/event")
@Validated
public class SecurityEventController {
    
    @Autowired
    private SecurityEventService service;

    @GetMapping
    public Page<SecurityEvent> getSecurityEventList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "eventTime,desc") String[] sort,
        // 查询条件参数
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String startTime,
        @RequestParam(required = false) String endTime,
        @RequestParam(required = false) String systemName,
        @RequestParam(required = false) String ipAddress,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String source
    ) {
        // 构建查询条件
        Specification<SecurityEvent> spec = buildSpecification(
            keyword, startTime, endTime, systemName, ipAddress, status, source
        );
        
        // 构建分页和排序
        Pageable pageable = buildPageable(page, size, sort);
        
        return service.findAll(spec, pageable);
    }
    
    @GetMapping("/all")
    public List<SecurityEvent> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    
    public ResponseEntity<SecurityEvent> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    
    public ResponseEntity<SecurityEvent> create(@RequestBody SecurityEvent entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    
    public ResponseEntity<SecurityEvent> update(@PathVariable Long id, @RequestBody SecurityEvent entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
    
    @DeleteMapping("/{id}")
    
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 批量删除记录
     * @param ids ID集合
     * @return 无内容响应
     */
    @DeleteMapping
    @AnonymousAccess
    public ResponseEntity<Void> deleteAll(@RequestBody Set<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 构建查询条件
     */
    private Specification<SecurityEvent> buildSpecification(
        String keyword, String startTime, String endTime,
        String systemName, String ipAddress, String status, String source
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索 (搜索ID、系统名称、IP地址、内容)
            if (StringUtils.hasText(keyword)) {
                Predicate keywordPredicate = cb.or(
                    cb.like(root.get("id").as(String.class), "%" + keyword + "%"),
                    cb.like(cb.lower(root.get("systemName")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("ipAddress")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("content")), "%" + keyword.toLowerCase() + "%")
                );
                predicates.add(keywordPredicate);
            }
            
            // 时间范围查询
            if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
                try {
                    LocalDateTime start = LocalDateTime.parse(startTime);
                    LocalDateTime end = LocalDateTime.parse(endTime);
                    predicates.add(cb.between(
                        root.get("eventTime").as(LocalDateTime.class), 
                        start, end
                    ));
                } catch (Exception e) {
                    // 时间解析错误处理
                }
            }
            
            // 字符串字段精确查询
            if (StringUtils.hasText(systemName)) {
                predicates.add(cb.like(cb.lower(root.get("systemName")), "%" + systemName.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(ipAddress)) {
                predicates.add(cb.like(root.get("ipAddress"), "%" + ipAddress + "%"));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(source)) {
                predicates.add(cb.like(cb.lower(root.get("source")), "%" + source.toLowerCase() + "%"));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 构建分页和排序
     */
    private Pageable buildPageable(int page, int size, String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        
        for (String sortStr : sort) {
            String[] parts = sortStr.split(",");
            if (parts.length == 0 || parts[0].trim().isEmpty()) {
                continue; // Skip empty sort parameters
            }
            
            String property = parts[0].trim();
            Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
            
            // Validate property exists in SecurityEvent
            if (isValidSortProperty(property)) {
                orders.add(new Sort.Order(direction, property));
            }
        }
        
        // If no valid sort orders, use default
        if (orders.isEmpty()) {
            orders.add(new Sort.Order(Sort.Direction.DESC, "eventTime"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
    
    /**
     * 验证排序属性是否有效
     */
    private boolean isValidSortProperty(String property) {
        // SecurityEvent的有效字段
        return "id".equals(property) || 
               "systemName".equals(property) || 
               "ipAddress".equals(property) || 
               "eventTime".equals(property) || 
               "status".equals(property) || 
               "source".equals(property) || 
               "createdAt".equals(property) || 
               "updatedAt".equals(property);
    }
}