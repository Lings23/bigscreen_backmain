package me.zhengjie.modules.stat.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.zhengjie.modules.stat.domain.AssetStat;
import me.zhengjie.modules.stat.dto.AssetStatQueryCriteria;
import me.zhengjie.modules.stat.dto.PageResult;
import me.zhengjie.modules.stat.service.AssetStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Api(tags = "资产统计数据查询接口")
@RestController
@RequestMapping("/api/stat/asset")
@Validated
public class AssetStatController {
    
    @Autowired
    private AssetStatService service;

    /**
     * 标准CRUD分页查询接口
     * @param page 页码，从0开始
     * @param size 每页大小，默认20
     * @param sort 排序字段，格式：field,direction
     * @param keyword 关键字搜索（统计日期）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param statDate 统计日期
     * @param networkDevice 网络设备数量
     * @param securityDevice 安全设备数量
     * @param domainName 域名数量
     * @param middleware 中间件数量
     * @param serviceCount 服务数量
     * @param application 应用数量
     * @param website 网站数量
     * @param virtualDevice 虚拟设备数量
     * @param port 端口数量
     * @param host 主机数量
     * @param databaseCount 数据库数量
     * @param osCount 操作系统数量
     * @return 分页结果
     */
    @GetMapping
    public ResponseEntity<Page<AssetStat>> getAssetStatList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate,
            @RequestParam(required = false) Integer networkDevice,
            @RequestParam(required = false) Integer securityDevice,
            @RequestParam(required = false) Integer domainName,
            @RequestParam(required = false) Integer middleware,
            @RequestParam(required = false) Integer serviceCount,
            @RequestParam(required = false) Integer application,
            @RequestParam(required = false) Integer website,
            @RequestParam(required = false) Integer virtualDevice,
            @RequestParam(required = false) Integer port,
            @RequestParam(required = false) Integer host,
            @RequestParam(required = false) Integer databaseCount,
            @RequestParam(required = false) Integer osCount) {

        // 构建动态查询条件
        Specification<AssetStat> spec = buildSpecification(keyword, startDate, endDate, statDate, networkDevice, securityDevice, domainName, middleware, serviceCount, application, website, virtualDevice, port, host, databaseCount, osCount);

        // 构建分页对象
        Pageable pageable = buildPageable(page, size, sort);

        // 执行查询
        Page<AssetStat> result = service.findAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有记录（保持向后兼容）
     */
    @GetMapping("/all")
    public List<AssetStat> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetStat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    
    @PostMapping
    public ResponseEntity<AssetStat> create(@RequestBody AssetStat stat) {
        return new ResponseEntity<>(service.save(stat), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AssetStat> update(@PathVariable Long id, @RequestBody AssetStat stat) {
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
    
    // ==================== 新增查询接口 ====================
    
    /**
     * 分页查询所有资产统计数据
     */
//    @ApiOperation("分页查询资产统计数据")
//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "page", value = "页码", defaultValue = "0", dataType = "int"),
//        @ApiImplicitParam(name = "size", value = "页大小(固定20)", defaultValue = "20", dataType = "int")
//    })
//    @GetMapping("/page")
//    public ResponseEntity<PageResult<AssetStat>> getPage(
//            @RequestParam(defaultValue = "0") @Min(0) Integer page,
//            @RequestParam(defaultValue = "20") Integer size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "statDate"));
//        Page<AssetStat> result = service.findAll(pageable);
//        return ResponseEntity.ok(PageResult.of(result));
//    }
    
    /**
     * 按时间段查询资产统计数据
     */
    @ApiOperation("按时间段查询资产统计数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "startDate", value = "开始日期", required = true, dataType = "date"),
        @ApiImplicitParam(name = "endDate", value = "结束日期", required = true, dataType = "date"),
        @ApiImplicitParam(name = "page", value = "页码", defaultValue = "0", dataType = "int"),
        @ApiImplicitParam(name = "size", value = "页大小(固定20)", defaultValue = "20", dataType = "int")
    })
    @GetMapping("/period")
    public ResponseEntity<PageResult<AssetStat>> queryByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<AssetStat> result = service.findByTimePeriod(startDate.atStartOfDay(), endDate.atTime(23, 59, 59), page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }
    
    /**
     * 关键字搜索资产统计数据
     */
    @ApiOperation("关键字搜索资产统计数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "keyword", value = "搜索关键字", required = true, dataType = "string"),
        @ApiImplicitParam(name = "page", value = "页码", defaultValue = "0", dataType = "int"),
        @ApiImplicitParam(name = "size", value = "页大小(固定20)", defaultValue = "20", dataType = "int")
    })
    @GetMapping("/search")
    public ResponseEntity<PageResult<AssetStat>> searchByKey(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<AssetStat> result = service.findByKeyword(keyword, page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }
    
    /**
     * 高级查询资产统计数据
     */
    @ApiOperation("高级查询资产统计数据")
    @PostMapping("/query")
    public ResponseEntity<PageResult<AssetStat>> queryByCriteria(
            @RequestBody AssetStatQueryCriteria criteria,
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, 20, 
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "statDate"));
        Page<AssetStat> result = service.findByCriteria(criteria, pageable);
        return ResponseEntity.ok(PageResult.of(result));
    }

    /**
     * 构建动态查询条件
     */
    private Specification<AssetStat> buildSpecification(String keyword, LocalDateTime startDate, LocalDateTime endDate,
                                                       LocalDate statDate, Integer networkDevice, Integer securityDevice, Integer domainName, Integer middleware, Integer serviceCount, Integer application, Integer website, Integer virtualDevice, Integer port, Integer host, Integer databaseCount, Integer osCount) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索 - 尝试解析为日期搜索统计日期
            if (keyword != null && !keyword.trim().isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(keyword.trim());
                    predicates.add(criteriaBuilder.equal(root.get("statDate"), date));
                } catch (Exception e) {
                    // 如果无法解析为日期，忽略关键字搜索
                }
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
            
            // 各种设备数量精确匹配
            if (networkDevice != null) {
                predicates.add(criteriaBuilder.equal(root.get("networkDevice"), networkDevice));
            }
            if (securityDevice != null) {
                predicates.add(criteriaBuilder.equal(root.get("securityDevice"), securityDevice));
            }
            if (domainName != null) {
                predicates.add(criteriaBuilder.equal(root.get("domainName"), domainName));
            }
            if (middleware != null) {
                predicates.add(criteriaBuilder.equal(root.get("middleware"), middleware));
            }
            if (serviceCount != null) {
                                predicates.add(criteriaBuilder.equal(root.get("serviceCount"), serviceCount));
            }
            if (application != null) {
                predicates.add(criteriaBuilder.equal(root.get("application"), application));
            }
            if (website != null) {
                predicates.add(criteriaBuilder.equal(root.get("website"), website));
            }
            if (virtualDevice != null) {
                predicates.add(criteriaBuilder.equal(root.get("virtualDevice"), virtualDevice));
            }
            if (port != null) {
                predicates.add(criteriaBuilder.equal(root.get("port"), port));
            }
            if (host != null) {
                predicates.add(criteriaBuilder.equal(root.get("host"), host));
            }
            if (databaseCount != null) {
                predicates.add(criteriaBuilder.equal(root.get("databaseCount"), databaseCount));
            }
            if (osCount != null) {
                predicates.add(criteriaBuilder.equal(root.get("osCount"), osCount));
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
        List<String> validSortFields = Arrays.asList("id", "statDate", "networkDevice", "securityDevice", "domainName", "middleware", "service", "application", "website", "virtualDevice", "port", "host", "databaseCount", "osCount", "createdAt", "updatedAt");
        
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