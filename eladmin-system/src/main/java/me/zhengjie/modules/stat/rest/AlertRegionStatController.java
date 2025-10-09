package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.AlertRegionStat;
import me.zhengjie.modules.stat.service.AlertRegionStatService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/region")
public class AlertRegionStatController {
    @Autowired
    private AlertRegionStatService service;

    /**
     * 标准CRUD分页查询接口
     * @param page 页码，从0开始
     * @param size 每页大小，默认20
     * @param sort 排序字段，格式：field,direction
     * @param keyword 关键字搜索
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param statDate 统计日期
     * @param regionName 区域名称
     * @param regionType 区域类型
     * @param alertCount 告警数量
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<AlertRegionStat>> getAlertRegionList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate,
            @RequestParam(required = false) String regionName,
            @RequestParam(required = false) String regionType,
            @RequestParam(required = false) Integer alertCount) {
        
        // 构建动态查询条件
        Specification<AlertRegionStat> spec = buildSpecification(keyword, startDate, endDate, statDate, regionName, regionType, alertCount);
        
        // 构建分页对象
        Pageable pageable = buildPageable(page, size, sort);
        
        // 执行查询
        Page<AlertRegionStat> result = service.findAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有记录（保持向后兼容）
     */
    @GetMapping("/all")
    public List<AlertRegionStat> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AlertRegionStat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<AlertRegionStat> create(@RequestBody AlertRegionStat stat) {
        return new ResponseEntity<>(service.save(stat), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AlertRegionStat> update(@PathVariable Long id, @RequestBody AlertRegionStat stat) {
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
    private Specification<AlertRegionStat> buildSpecification(String keyword, LocalDateTime startDate, LocalDateTime endDate, 
                                                             LocalDate statDate, String regionName, String regionType, Integer alertCount) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索 - 搜索区域名称和区域类型
            if (keyword != null && !keyword.trim().isEmpty()) {
                String keywordLower = "%" + keyword.toLowerCase() + "%";
                Predicate regionNamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("regionName")), keywordLower
                );
                Predicate regionTypePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("regionType")), keywordLower
                );
                predicates.add(criteriaBuilder.or(regionNamePredicate, regionTypePredicate));
            }
            
            // 创建时间范围查询
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            // 统计日期精确匹配
            if (statDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("statDate"), statDate));
            }
            
            // 区域名称模糊匹配
            if (regionName != null && !regionName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("regionName")), 
                    "%" + regionName.toLowerCase() + "%"
                ));
            }
            
            // 区域类型模糊匹配
            if (regionType != null && !regionType.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("regionType")), 
                    "%" + regionType.toLowerCase() + "%"
                ));
            }
            
            // 告警数量精确匹配
            if (alertCount != null) {
                predicates.add(criteriaBuilder.equal(root.get("alertCount"), alertCount));
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
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "statDate"));
        }
        
        List<Sort.Order> orders = new ArrayList<>();
        List<String> validSortFields = Arrays.asList("id", "regionName", "regionType", "alertCount", "statDate", "createdAt");
        
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
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "statDate"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
} 