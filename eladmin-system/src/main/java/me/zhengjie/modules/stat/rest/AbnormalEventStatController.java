package me.zhengjie.modules.stat.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import me.zhengjie.modules.stat.domain.AbnormalEventStat;
import me.zhengjie.modules.stat.dto.AbnormalEventStatQueryCriteria;
import me.zhengjie.modules.stat.dto.PageResult;
import me.zhengjie.modules.stat.service.AbnormalEventStatService;
import me.zhengjie.modules.stat.service.impl.AbnormalEventStatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Api(tags = "异常事件统计管理")
@RestController
@RequestMapping("/api/stat/abnormal")
@Validated
public class AbnormalEventStatController {
    
    @Autowired
    private AbnormalEventStatService service;
    
    @Autowired
    private AbnormalEventStatServiceImpl serviceImpl;

    @ApiOperation("获取所有异常事件统计")
    @GetMapping
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
        Page<AbnormalEventStat> result = serviceImpl.findAll(page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }

    @ApiOperation("按时间段查询异常事件统计")
    @GetMapping("/time-period")
    public ResponseEntity<PageResult<AbnormalEventStat>> getByTimePeriod(
            @ApiParam(value = "开始日期", example = "2023-01-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @ApiParam(value = "结束日期", example = "2023-12-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ApiParam(value = "页码", example = "0") @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @ApiParam(value = "每页大小", example = "20") @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Page<AbnormalEventStat> result = serviceImpl.findByTimePeriod(startDate, endDate, page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }

    @ApiOperation("关键字搜索异常事件统计")
    @GetMapping("/search")
    public ResponseEntity<PageResult<AbnormalEventStat>> searchByKeyword(
            @ApiParam(value = "搜索关键字") @RequestParam String keyword,
            @ApiParam(value = "页码", example = "0") @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @ApiParam(value = "每页大小", example = "20") @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Page<AbnormalEventStat> result = serviceImpl.findByKeyword(keyword, page, size);
        return ResponseEntity.ok(PageResult.of(result));
    }

    @ApiOperation("高级条件查询异常事件统计")
    @PostMapping("/criteria")
    public ResponseEntity<PageResult<AbnormalEventStat>> findByCriteria(
            @Valid @RequestBody AbnormalEventStatQueryCriteria criteria,
            @ApiParam(value = "页码", example = "0") @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @ApiParam(value = "每页大小", example = "20") @RequestParam(defaultValue = "20") @Min(1) Integer size) {
        Page<AbnormalEventStat> result = serviceImpl.findByCriteria(criteria, serviceImpl.createPageable(page, size));
        return ResponseEntity.ok(PageResult.of(result));
    }
} 