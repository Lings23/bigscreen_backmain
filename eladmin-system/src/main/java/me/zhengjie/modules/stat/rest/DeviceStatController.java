package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.DeviceStat;
import me.zhengjie.modules.stat.service.DeviceStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/device")
public class DeviceStatController {
    @Autowired
    private DeviceStatService service;

    @GetMapping
    public Page<DeviceStat> getDeviceList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "statTime,desc") String[] sort,
        // 查询条件参数
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String startTime,
        @RequestParam(required = false) String endTime,
        @RequestParam(required = false) Integer onlineCount,
        @RequestParam(required = false) Integer offlineCount,
        @RequestParam(required = false) Integer alarmCount
    ) {
        // 构建查询条件
        Specification<DeviceStat> spec = buildSpecification(
            keyword, startTime, endTime, onlineCount, offlineCount, alarmCount
        );
        
        // 构建分页和排序
        Pageable pageable = buildPageable(page, size, sort);
        
        return service.findAll(spec, pageable);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<DeviceStat>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceStat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/range")
    public ResponseEntity<List<DeviceStat>> getByTimeRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(service.getByTimeRange(startTime, endTime));
    }

    @PostMapping
    public ResponseEntity<DeviceStat> create(@RequestBody DeviceStat stat) {
        return new ResponseEntity<>(service.save(stat), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DeviceStat> update(@PathVariable Long id, @RequestBody DeviceStat stat) {
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
    private Specification<DeviceStat> buildSpecification(
        String keyword, String startTime, String endTime,
        Integer onlineCount, Integer offlineCount, Integer alarmCount
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
            
            // 时间范围查询
            if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
                try {
                    LocalDateTime start = LocalDateTime.parse(startTime);
                    LocalDateTime end = LocalDateTime.parse(endTime);
                    predicates.add(cb.between(
                        root.get("statTime").as(LocalDateTime.class), 
                        start, end
                    ));
                } catch (Exception e) {
                    // 时间解析错误处理
                }
            }
            
            // 数值字段精确查询
            if (onlineCount != null) {
                predicates.add(cb.equal(root.get("onlineCount"), onlineCount));
            }
            if (offlineCount != null) {
                predicates.add(cb.equal(root.get("offlineCount"), offlineCount));
            }
            if (alarmCount != null) {
                predicates.add(cb.equal(root.get("alarmCount"), alarmCount));
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
            
            // Validate property exists in DeviceStat
            if (isValidSortProperty(property)) {
                orders.add(new Sort.Order(direction, property));
            }
        }
        
        // If no valid sort orders, use default
        if (orders.isEmpty()) {
            orders.add(new Sort.Order(Sort.Direction.DESC, "statTime"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
    
    /**
     * 验证排序属性是否有效
     */
    private boolean isValidSortProperty(String property) {
        // DeviceStat的有效字段
        return "id".equals(property) || 
               "statTime".equals(property) || 
               "onlineCount".equals(property) || 
               "offlineCount".equals(property) || 
               "alarmCount".equals(property) || 
               "createdAt".equals(property) || 
               "updatedAt".equals(property);
    }
}