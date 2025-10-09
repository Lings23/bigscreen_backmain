package me.zhengjie.modules.stat.rest;

import me.zhengjie.annotation.rest.AnonymousAccess;
import me.zhengjie.modules.stat.domain.DutySchedule;
import me.zhengjie.modules.stat.service.DutyScheduleService;
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
@RequestMapping("/api/stat/duty")
public class DutyScheduleController {
    @Autowired
    private DutyScheduleService service;

    /**
     * 标准CRUD分页查询接口
     * @param page 页码，从0开始
     * @param size 每页大小，默认20
     * @param sort 排序字段，格式：field,direction
     * @param keyword 关键字搜索
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param orgName 组织名称
     * @param leaderName 负责人姓名
     * @param dutyDate 值班日期
     * @param eventName 事件名称
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<DutySchedule>> getDutyScheduleList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String orgName,
            @RequestParam(required = false) String leaderName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dutyDate,
            @RequestParam(required = false) String eventName) {
        
        // 构建动态查询条件
        Specification<DutySchedule> spec = buildSpecification(keyword, startDate, endDate, orgName, leaderName, dutyDate, eventName);
        
        // 构建分页对象
        Pageable pageable = buildPageable(page, size, sort);
        
        // 执行查询
        Page<DutySchedule> result = service.findAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有记录（保持向后兼容）
     */
    @GetMapping("/all")
    public List<DutySchedule> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    
    public ResponseEntity<DutySchedule> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    
    public ResponseEntity<DutySchedule> create(@RequestBody DutySchedule entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    
    public ResponseEntity<DutySchedule> update(@PathVariable Long id, @RequestBody DutySchedule entity) {
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
    public ResponseEntity<Void> deleteAll(@RequestBody Set<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 构建动态查询条件
     */
    private Specification<DutySchedule> buildSpecification(String keyword, LocalDateTime startDate, LocalDateTime endDate,
                                                          String orgName, String leaderName, LocalDate dutyDate, String eventName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索 - 在组织名称、负责人姓名、值班人员中搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("orgName")), likePattern.toLowerCase()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("leaderName")), likePattern.toLowerCase()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("dutyPerson")), likePattern.toLowerCase())
                );
                predicates.add(keywordPredicate);
            }
            
            // 创建时间范围查询
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            // 组织名称精确匹配
            if (orgName != null && !orgName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("orgName"), orgName));
            }
            
            // 负责人姓名精确匹配
            if (leaderName != null && !leaderName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("leaderName"), leaderName));
            }
            
            // 值班日期精确匹配
            if (dutyDate != null) {
                predicates.add(criteriaBuilder.equal(root.get("dutyDate"), dutyDate));
            }
            
            // 事件名称精确匹配
            if (eventName != null && !eventName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("eventName"), eventName));
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
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dutyDate"));
        }
        
        List<Sort.Order> orders = new ArrayList<>();
        List<String> validSortFields = Arrays.asList("id", "orgName", "leaderName", "leaderPhone", "dutyPerson", "dutyPhone", "dutyDate", "eventName", "createdAt", "updatedAt");
        
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
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dutyDate"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
} 