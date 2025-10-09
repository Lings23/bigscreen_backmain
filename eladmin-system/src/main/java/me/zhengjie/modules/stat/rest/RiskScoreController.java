package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.RiskScore;
import me.zhengjie.modules.stat.service.RiskScoreService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/risk")
public class RiskScoreController {
    @Autowired
    private RiskScoreService service;

    /**
     * 标准CRUD分页查询接口
     * @param page 页码，从0开始
     * @param size 每页大小，默认20
     * @param sort 排序字段，格式：field,direction
     * @param keyword 关键字搜索
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param scoreDate 评分日期
     * @param systemName 系统名称
     * @param riskScore 风险评分
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<RiskScore>> getRiskScoreList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scoreDate,
            @RequestParam(required = false) String systemName,
            @RequestParam(required = false) BigDecimal riskScore) {
        
        // 构建动态查询条件
        Specification<RiskScore> spec = buildSpecification(keyword, startDate, endDate, scoreDate, systemName, riskScore);
        
        // 构建分页对象
        Pageable pageable = buildPageable(page, size, sort);
        
        // 执行查询
        Page<RiskScore> result = service.findAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有记录（保持向后兼容）
     */
    @GetMapping("/all")
    public List<RiskScore> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RiskScore> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<RiskScore> create(@RequestBody RiskScore stat) {
        return new ResponseEntity<>(service.save(stat), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RiskScore> update(@PathVariable Long id, @RequestBody RiskScore stat) {
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
     * 构建动态查询条件
     */
    private Specification<RiskScore> buildSpecification(String keyword, LocalDateTime startDate, LocalDateTime endDate, 
                                                       LocalDate scoreDate, String systemName, BigDecimal riskScore) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索 - 搜索系统名称
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("systemName")), 
                    "%" + keyword.toLowerCase() + "%"
                ));
            }
            
            // 创建时间范围查询
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            // 评分日期精确匹配
            if (scoreDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("scoreDate"), scoreDate));
            }
            
            // 系统名称模糊匹配
            if (systemName != null && !systemName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("systemName")), 
                    "%" + systemName.toLowerCase() + "%"
                ));
            }
            
            // 风险评分精确匹配
            if (riskScore != null) {
                predicates.add(criteriaBuilder.equal(root.get("riskScore"), riskScore));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 构建分页对象
     */
    private Pageable buildPageable(Integer page, Integer size, String[] sort) {
        // 验证并设置默认值
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size <= 0) {
            size = 20;
        }
        
        // 处理排序
        if (sort == null || sort.length == 0) {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scoreDate"));
        }
        
        List<Sort.Order> orders = new ArrayList<>();
        List<String> validSortFields = Arrays.asList("id", "systemName", "riskScore", "scoreDate", "createdAt", "updatedAt");
        
        for (String sortParam : sort) {
            if (sortParam != null && !sortParam.trim().isEmpty()) {
                String[] parts = sortParam.split(",");
                String field = parts[0].trim();
                
                // 验证排序字段
                if (validSortFields.contains(field)) {
                    Sort.Direction direction = Sort.Direction.ASC;
                    if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
                        direction = Sort.Direction.DESC;
                    }
                    orders.add(new Sort.Order(direction, field));
                }
            }
        }
        
        // 如果没有有效的排序字段，使用默认排序
        if (orders.isEmpty()) {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scoreDate"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
} 