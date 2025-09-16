package me.zhengjie.modules.stat.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.zhengjie.modules.stat.domain.AssetStat;
import me.zhengjie.modules.stat.dto.AssetStatQueryCriteria;
import me.zhengjie.modules.stat.dto.PageResult;
import me.zhengjie.modules.stat.service.AssetStatService;
import me.zhengjie.modules.stat.service.impl.AssetStatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Api(tags = "资产统计数据查询接口")
@RestController
@RequestMapping("/api/stat/asset")
@Validated
public class AssetStatController {
    
    @Autowired
    private AssetStatService service;
    
    @Autowired
    private AssetStatServiceImpl serviceImpl;

    @GetMapping
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
    @ApiOperation("分页查询资产统计数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "page", value = "页码", defaultValue = "0", dataType = "int"),
        @ApiImplicitParam(name = "size", value = "页大小(固定20)", defaultValue = "20", dataType = "int")
    })
    @GetMapping("/page")
    public ResponseEntity<PageResult<AssetStat>> getPage(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<AssetStat> result = serviceImpl.findAll(page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }
    
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
        Page<AssetStat> result = serviceImpl.findByTimePeriod(startDate, endDate, page, size);
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
        Page<AssetStat> result = serviceImpl.findByKeyword(keyword, page, size);
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
        Page<AssetStat> result = serviceImpl.findByCriteria(criteria, pageable);
        return ResponseEntity.ok(PageResult.of(result));
    }
} 