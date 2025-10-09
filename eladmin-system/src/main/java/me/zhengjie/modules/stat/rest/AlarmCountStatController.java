package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.AlarmCountStat;
import me.zhengjie.modules.stat.service.AlarmCountStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/alarm")
public class AlarmCountStatController {
    @Autowired
    private AlarmCountStatService service;

    @GetMapping
    public Page<AlarmCountStat> getAlarmList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "statDate,desc") String[] sort,
        // 查询条件参数
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer statYear,
        @RequestParam(required = false) Integer statQuarter,
        @RequestParam(required = false) Integer successCount,
        @RequestParam(required = false) Integer suspiciousCount,
        @RequestParam(required = false) Integer attemptCount
    ) {
        // 构建查询条件
        Specification<AlarmCountStat> spec = buildSpecification(
            keyword, statYear, statQuarter, successCount, suspiciousCount, attemptCount
        );
        
        // 构建分页和排序
        Pageable pageable = buildPageable(page, size, sort);
        
        return service.findAll(spec, pageable);
    }
    
    @GetMapping("/all")
    public List<AlarmCountStat> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AlarmCountStat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<AlarmCountStat> create(@RequestBody AlarmCountStat stat) {
        return new ResponseEntity<>(service.save(stat), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AlarmCountStat> update(@PathVariable Long id, @RequestBody AlarmCountStat stat) {
        return ResponseEntity.ok(service.update(id, stat));
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
    public ResponseEntity<Void> deleteAll(@RequestBody Set<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 构建查询条件
     */
    private Specification<AlarmCountStat> buildSpecification(
        String keyword, Integer statYear, Integer statQuarter,
        Integer successCount, Integer suspiciousCount, Integer attemptCount
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索 (搜索ID)
            if (StringUtils.hasText(keyword)) {
                try {
                    // 尝试作为ID搜索
                    Long id = Long.parseLong(keyword);
                    predicates.add(cb.equal(root.get("id"), id));
                } catch (NumberFormatException e) {
                    // 如果不是数字，添加一个永远为false的条件
                    predicates.add(cb.disjunction());
                }
            }
            
            // 年份查询 - 基于statDate字段提取年份
            if (statYear != null) {
                predicates.add(cb.equal(
                    cb.function("YEAR", Integer.class, root.get("statDate")), 
                    statYear
                ));
            }
            
            // 季度查询 - 基于statDate字段提取季度
            if (statQuarter != null && statQuarter >= 1 && statQuarter <= 4) {
                predicates.add(cb.equal(
                    cb.function("QUARTER", Integer.class, root.get("statDate")), 
                    statQuarter
                ));
            }
            
            // 数值字段精确查询
            if (successCount != null) {
                predicates.add(cb.equal(root.get("successCount"), successCount));
            }
            if (suspiciousCount != null) {
                predicates.add(cb.equal(root.get("suspiciousCount"), suspiciousCount));
            }
            if (attemptCount != null) {
                predicates.add(cb.equal(root.get("attemptCount"), attemptCount));
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
            
            // Validate property exists in AlarmCountStat
            if (isValidSortProperty(property)) {
                orders.add(new Sort.Order(direction, property));
            }
        }
        
        // If no valid sort orders, use default
        if (orders.isEmpty()) {
            orders.add(new Sort.Order(Sort.Direction.DESC, "statDate"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
    
    /**
     * 验证排序属性是否有效
     */
    private boolean isValidSortProperty(String property) {
        // AlarmCountStat的有效字段
        return "id".equals(property) || 
               "statDate".equals(property) || 
               "successCount".equals(property) || 
               "suspiciousCount".equals(property) || 
               "attemptCount".equals(property) || 
               "createdAt".equals(property) || 
               "updatedAt".equals(property);
    }
} 