package me.zhengjie.modules.stat.rest;

import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousAccess;
import me.zhengjie.modules.stat.domain.AssetList;
import me.zhengjie.modules.stat.dto.AssetListDto;
import me.zhengjie.modules.stat.dto.AssetListQueryDto;
import me.zhengjie.modules.stat.service.AssetListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 资产列表Controller
 * @author system
 * @date 2025-08-01
 */
@RestController
@RequestMapping("/api/asset-list")
@RequiredArgsConstructor
public class AssetListController {

    private final AssetListService assetListService;

    /**
     * 创建资产
     */
    @Log("创建资产")
    @PostMapping
    @PreAuthorize("@el.check('asset:add')")
    public ResponseEntity<AssetListDto> createAsset(@Validated @RequestBody AssetListDto assetListDto) {
        AssetListDto result = assetListService.createAsset(assetListDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 更新资产
     */
    @Log("更新资产")
    @PutMapping("/{id}")
    @PreAuthorize("@el.check('asset:edit')")
    public ResponseEntity<AssetListDto> updateAsset(@PathVariable Long id, @Validated @RequestBody AssetListDto assetListDto) {
        AssetListDto result = assetListService.updateAsset(id, assetListDto);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除资产
     */
    @Log("删除资产")
    @DeleteMapping("/{id}")
    @PreAuthorize("@el.check('asset:del')")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetListService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除资产
     */
    @Log("批量删除资产")
    @DeleteMapping
    @PreAuthorize("@el.check('asset:del')")
    public ResponseEntity<Void> deleteAssets(@RequestBody List<Long> ids) {
        assetListService.deleteAssets(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据ID获取资产
     */
    @GetMapping("/{id}")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<AssetListDto> getAssetById(@PathVariable Long id) {
        AssetListDto result = assetListService.getAssetById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据IP和端口获取资产
     */
    @GetMapping("/ip-port")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<AssetListDto> getAssetByIpAndPort(@RequestParam String assetIp, @RequestParam Integer assetPort) {
        AssetListDto result = assetListService.getAssetByIpAndPort(assetIp, assetPort);
        return ResponseEntity.ok(result);
    }

    /**
     * 标准CRUD分页查询接口
     * @param page 页码，从0开始
     * @param size 每页大小，默认20
     * @param sort 排序字段，格式：field,direction
     * @param keyword 关键字搜索
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param assetIp 资产IP
     * @param assetPort 资产端口
     * @param systemName 系统名称
     * @param organizationName 所属单位
     * @return 分页结果
     */
    @GetMapping
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<Page<AssetList>> getAssetList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String assetIp,
            @RequestParam(required = false) Integer assetPort,
            @RequestParam(required = false) String systemName,
            @RequestParam(required = false) String organizationName) {
        
        // 构建动态查询条件
        Specification<AssetList> spec = buildSpecification(keyword, startDate, endDate, assetIp, assetPort, systemName, organizationName);
        
        // 构建分页对象
        Pageable pageable = buildPageable(page, size, sort);
        
        // 执行查询
        Page<AssetList> result = assetListService.findAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * DTO分页查询资产列表（保持向后兼容）
     */
    @GetMapping("/dto")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<Page<AssetListDto>> getAssetListDto(AssetListQueryDto queryDto) {
        Page<AssetListDto> result = assetListService.getAssetList(queryDto);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有资产列表
     */
    @GetMapping("/all")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<List<AssetListDto>> getAllAssets() {
        List<AssetListDto> result = assetListService.getAllAssets();
        return ResponseEntity.ok(result);
    }

    /**
     * 根据条件查询资产列表
     */
    @GetMapping("/search")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<List<AssetListDto>> getAssetsByConditions(
            @RequestParam(required = false) String assetIp,
            @RequestParam(required = false) String systemName,
            @RequestParam(required = false) String organizationName) {
        List<AssetListDto> result = assetListService.getAssetsByConditions(assetIp, systemName, organizationName);
        return ResponseEntity.ok(result);
    }

    /**
     * 统计资产总数
     */
    @GetMapping("/count")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<Long> countAssets() {
        long count = assetListService.countAssets();
        return ResponseEntity.ok(count);
    }

    /**
     * 检查IP和端口组合是否存在
     */
    @GetMapping("/exists")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<Boolean> existsByIpAndPort(@RequestParam String assetIp, @RequestParam Integer assetPort) {
        boolean exists = assetListService.existsByIpAndPort(assetIp, assetPort);
        return ResponseEntity.ok(exists);
    }

    /**
     * 导出资产列表
     */
    @Log("导出资产列表")
    @GetMapping("/download")
    @PreAuthorize("@el.check('asset:list')")
    public void download(HttpServletResponse response) throws IOException {
        assetListService.download(response);
    }

    /**
     * 构建动态查询条件
     */
    private Specification<AssetList> buildSpecification(String keyword, LocalDateTime startDate, LocalDateTime endDate,
                                                       String assetIp, Integer assetPort, String systemName, String organizationName) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 关键字搜索 - 在资产IP、系统名称、所属单位中搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("assetIp")), likePattern.toLowerCase()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("systemName")), likePattern.toLowerCase()),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("organizationName")), likePattern.toLowerCase())
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
            
            // 资产IP精确匹配或模糊匹配
            if (assetIp != null && !assetIp.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("assetIp"), "%" + assetIp + "%"));
            }
            
            // 资产端口精确匹配
            if (assetPort != null) {
                predicates.add(criteriaBuilder.equal(root.get("assetPort"), assetPort));
            }
            
            // 系统名称模糊匹配
            if (systemName != null && !systemName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("systemName"), "%" + systemName + "%"));
            }
            
            // 所属单位模糊匹配
            if (organizationName != null && !organizationName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("organizationName"), "%" + organizationName + "%"));
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
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        List<Sort.Order> orders = new ArrayList<>();
        List<String> validSortFields = Arrays.asList("id", "assetIp", "assetPort", "systemName", "organizationName", "createdAt", "updatedAt");
        
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
            return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        return PageRequest.of(page, size, Sort.by(orders));
    }
} 