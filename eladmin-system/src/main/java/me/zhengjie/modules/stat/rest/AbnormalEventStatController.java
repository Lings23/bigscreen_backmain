package me.zhengjie.modules.stat.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import me.zhengjie.modules.stat.domain.AbnormalEventStat;
import me.zhengjie.modules.stat.dto.AbnormalEventStatQueryCriteria;
import me.zhengjie.modules.stat.dto.PageResult;
import me.zhengjie.modules.stat.service.AbnormalEventStatService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Api(tags = "异常事件统计管理")
@RestController
@RequestMapping("/api/stat/abnormal")
@Validated
public class AbnormalEventStatController {
    
    @Autowired
    private AbnormalEventStatService service;

    @ApiOperation("标准CRUD查询接口 - 支持分页、排序和多条件查询")
    @GetMapping
    public Page<AbnormalEventStat> getAbnormalList(
            @ApiParam(value = "页码 (0-based)", example = "0") @RequestParam(defaultValue = "0") @Min(0) int page,
            @ApiParam(value = "每页条数", example = "10") @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @ApiParam(value = "排序规则数组", example = "statTime,desc") @RequestParam(defaultValue = "id,desc") String[] sort,
            // 查询条件参数
            @ApiParam(value = "搜索关键字") @RequestParam(required = false) String keyword,
            @ApiParam(value = "开始日期", example = "2023-01-01") @RequestParam(required = false) String startDate,
            @ApiParam(value = "结束日期", example = "2023-12-31") @RequestParam(required = false) String endDate,
            @ApiParam(value = "统计时间", example = "2023-06-15T10:30:00") @RequestParam(required = false) String statTime,
            @ApiParam(value = "出站数量") @RequestParam(required = false) Integer outbound,
            @ApiParam(value = "外到内数量") @RequestParam(required = false) Integer outsideToInside,
            @ApiParam(value = "横向移动数量") @RequestParam(required = false) Integer lateralMove,
            @ApiParam(value = "其他数量") @RequestParam(required = false) Integer other
    ) {
        // 构建查询条件
        Specification<AbnormalEventStat> spec = buildSpecification(
            keyword, startDate, endDate, statTime, 
            outbound, outsideToInside, lateralMove, other
        );
        
        // 构建分页和排序
        Pageable pageable = buildPageable(page, size, sort);
        
        return service.findAll(spec, pageable);
    }
    
    @ApiOperation("获取所有异常事件统计 - 兼容性接口")
    @GetMapping("/all")
    public List<AbnormalEventStat> getAll() {
        return service.getAll();
    }
    
    @ApiOperation("根据ID获取异常事件统计")
    @GetMapping("/{id}")
    public ResponseEntity<AbnormalEventStat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @ApiOperation("创建异常事件统计")
    @PostMapping
    public ResponseEntity<AbnormalEventStat> create(@Valid @RequestBody AbnormalEventStat entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }
    
    @ApiOperation("更新异常事件统计")
    @PutMapping("/{id}")
    public ResponseEntity<AbnormalEventStat> update(@PathVariable Long id, @Valid @RequestBody AbnormalEventStat entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }
    
    @ApiOperation("删除异常事件统计")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
    
    @ApiOperation("批量删除异常事件统计")
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@RequestBody Set<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("分页查询异常事件统计")
    @GetMapping("/page")
    public ResponseEntity<PageResult<AbnormalEventStat>> getPage(
            @ApiParam(value = "页码", example = "0") @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @ApiParam(value = "每页大小", example = "20") @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Page<AbnormalEventStat> result = service.findAll(page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }

    @ApiOperation("按时间段查询异常事件统计")
    @GetMapping("/time-period")
    public ResponseEntity<PageResult<AbnormalEventStat>> getByTimePeriod(
            @ApiParam(value = "开始日期", example = "2023-01-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "结束日期", example = "2023-12-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ApiParam(value = "页码", example = "0") @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @ApiParam(value = "每页大小", example = "20") @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Page<AbnormalEventStat> result = service.findByTimePeriod(startDate, endDate, page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }

    @ApiOperation("关键字搜索异常事件统计")
    @GetMapping("/search")
    public ResponseEntity<PageResult<AbnormalEventStat>> searchByKeyword(
            @ApiParam(value = "搜索关键字") @RequestParam String keyword,
            @ApiParam(value = "页码", example = "0") @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @ApiParam(value = "每页大小", example = "20") @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Page<AbnormalEventStat> result = service.findByKeyword(keyword, page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }

    @ApiOperation("高级条件查询异常事件统计")
    @PostMapping("/criteria")
    public ResponseEntity<PageResult<AbnormalEventStat>> findByCriteria(
            @Valid @RequestBody AbnormalEventStatQueryCriteria criteria,
            @ApiParam(value = "页码", example = "0") @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @ApiParam(value = "每页大小", example = "20") @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Page<AbnormalEventStat> result = service.findByCriteria(criteria, service.createPageable(page, size));
        return ResponseEntity.ok(PageResult.of(result));
    }
    
    /**
     * 构建动态查询条件
     */
    private Specification<AbnormalEventStat> buildSpecification(
        String keyword, String startDate, String endDate, String statTime,
        Integer outbound, Integer outsideToInside, Integer lateralMove, Integer other
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索 (可搜索多个字段)
            if (StringUtils.hasText(keyword)) {
                Predicate keywordPredicate = cb.or(
                    cb.like(root.get("id").as(String.class), "%" + keyword + "%"),
                    cb.like(root.get("statTime").as(String.class), "%" + keyword + "%")
                );
                predicates.add(keywordPredicate);
            }
            
            // 日期范围查询
            if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
                try {
                    LocalDate start = LocalDate.parse(startDate);
                    LocalDate end = LocalDate.parse(endDate);
                    predicates.add(cb.between(
                        root.get("statTime").as(LocalDate.class), 
                        start, end
                    ));
                } catch (Exception e) {
                    // 日期解析错误处理 - 忽略无效日期
                }
            }
            
            // 精确时间查询
            if (StringUtils.hasText(statTime)) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(statTime);
                    predicates.add(cb.equal(root.get("statTime"), dateTime));
                } catch (Exception e) {
                    // 时间解析错误处理 - 忽略无效时间
                }
            }
            
            // 数值字段查询
            if (outbound != null) {
                predicates.add(cb.equal(root.get("outbound"), outbound));
            }
            if (outsideToInside != null) {
                predicates.add(cb.equal(root.get("outsideToInside"), outsideToInside));
            }
            if (lateralMove != null) {
                predicates.add(cb.equal(root.get("lateralMove"), lateralMove));
            }
            if (other != null) {
                predicates.add(cb.equal(root.get("other"), other));
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
            
            // Validate property exists in AbnormalEventStat
            if (isValidSortProperty(property)) {
                orders.add(new Sort.Order(direction, property));
            }
        }
        
        // If no valid sort orders, use default
        if (orders.isEmpty()) {
            orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
    
    /**
     * 验证排序属性是否有效
     */
    private boolean isValidSortProperty(String property) {
        // AbnormalEventStat的有效字段
        return "id".equals(property) || 
               "statTime".equals(property) || 
               "outbound".equals(property) || 
               "outsideToInside".equals(property) || 
               "lateralMove".equals(property) || 
               "other".equals(property) || 
               "createdAt".equals(property) || 
               "updatedAt".equals(property);
    }
} 