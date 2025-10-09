package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.AttackFlyline;
import me.zhengjie.modules.stat.service.AttackFlylineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 
 */
@RestController
@RequestMapping("/api/stat/attack")
public class AttackFlylineController {
    @Autowired
    private AttackFlylineService service;

    /**
     * 标准CRUD分页查询接口
     * @param page 页码，从0开始
     * @param size 每页大小，默认10
     * @param sort 排序字段，格式：field,direction
     * @param sourceIp 源IP地址
     * @param targetIp 目标IP地址
     * @param sourceLocationName 源地理位置名称
     * @param targetLocationName 目标地理位置名称
     * @param sourceIsDomestic 攻击源是否为国内
     * @param attackTime 攻击时间范围，格式：["YYYY-MM-DD HH:mm:ss", "YYYY-MM-DD HH:mm:ss"]
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<AttackFlyline>> getAttackFlylineList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String sourceIp,
            @RequestParam(required = false) String targetIp,
            @RequestParam(required = false) String sourceLocationName,
            @RequestParam(required = false) String targetLocationName,
            @RequestParam(required = false) Boolean sourceIsDomestic,
            @RequestParam(required = false) String[] attackTime) {
        
        // 构建动态查询条件
        Specification<AttackFlyline> spec = buildSpecification(sourceIp, targetIp, sourceLocationName, targetLocationName, sourceIsDomestic, attackTime);
        
        // 构建分页对象
        Pageable pageable = buildPageable(page, size, sort);
        
        // 执行查询
        Page<AttackFlyline> result = service.findAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * 
     */
    @GetMapping("/all")
    public List<AttackFlyline> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AttackFlyline> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<AttackFlyline> create(@RequestBody AttackFlyline entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AttackFlyline> update(@PathVariable Long id, @RequestBody AttackFlyline entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 
     * @param ids ID
     * @return 
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@RequestBody Set<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 构建动态查询条件
     */
    private Specification<AttackFlyline> buildSpecification(String sourceIp, String targetIp, String sourceLocationName, 
                                                           String targetLocationName, Boolean sourceIsDomestic, String[] attackTime) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 源IP地址精确匹配
            if (sourceIp != null && !sourceIp.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("sourceIp"), sourceIp));
            }
            
            // 目标IP地址精确匹配
            if (targetIp != null && !targetIp.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("targetIp"), targetIp));
            }
            
            // 源地理位置名称精确匹配
            if (sourceLocationName != null && !sourceLocationName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("sourceLocationName"), sourceLocationName));
            }
            
            // 目标地理位置名称精确匹配
            if (targetLocationName != null && !targetLocationName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("targetLocationName"), targetLocationName));
            }
            
            // 攻击源是否为国内
            if (sourceIsDomestic != null) {
                predicates.add(criteriaBuilder.equal(root.get("sourceIsDomestic"), sourceIsDomestic));
            }
            
            // 攻击时间范围查询 - 处理数组格式 ["YYYY-MM-DD HH:mm:ss", "YYYY-MM-DD HH:mm:ss"]
            if (attackTime != null && attackTime.length == 2) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime startTime = LocalDateTime.parse(attackTime[0], formatter);
                    LocalDateTime endTime = LocalDateTime.parse(attackTime[1], formatter);
                    
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("attackTime"), startTime));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("attackTime"), endTime));
                } catch (Exception e) {
                    // 如果日期解析失败，忽略时间范围查询
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 
     */
    private Pageable buildPageable(Integer page, Integer size, String[] sort) {
        // 
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 20;
        }
        
        // 
        if (sort == null || sort.length == 0) {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "attackTime"));
        }
        
        List<Sort.Order> orders = new ArrayList<>();
        List<String> validSortFields = Arrays.asList("id", "sourceIp", "targetIp", "sourceLocationName", "targetLocationName", "attackMethod", "targetSystem", "attackTime", "sourceIsDomestic", "createdAt", "updatedAt");
        
        for (String sortParam : sort) {
            if (sortParam != null && !sortParam.trim().isEmpty()) {
                String[] parts = sortParam.split(",");
                String field = parts[0].trim();
                
                // 
                if (validSortFields.contains(field)) {
                    Sort.Direction direction = Sort.Direction.ASC;
                    if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
                        direction = Sort.Direction.DESC;
                    }
                    orders.add(new Sort.Order(direction, field));
                }
            }
        }
        
        // 
        if (orders.isEmpty()) {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "attackTime"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
}