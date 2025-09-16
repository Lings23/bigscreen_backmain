package me.zhengjie.modules.stat.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.zhengjie.annotation.rest.AnonymousAccess;
import me.zhengjie.modules.stat.domain.SecurityEvent;
import me.zhengjie.modules.stat.dto.PageResult;
import me.zhengjie.modules.stat.dto.SecurityEventQueryCriteria;
import me.zhengjie.modules.stat.service.SecurityEventService;
import me.zhengjie.modules.stat.service.impl.SecurityEventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Api(tags = "安全事件统计数据查询接口")
@RestController
@RequestMapping("/api/stat/event")
@Validated
public class SecurityEventController {
    
    @Autowired
    private SecurityEventService service;
    
    @Autowired
    private SecurityEventServiceImpl serviceImpl;

    @GetMapping
    
    public List<SecurityEvent> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    
    public ResponseEntity<SecurityEvent> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    
    public ResponseEntity<SecurityEvent> create(@RequestBody SecurityEvent entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    
    public ResponseEntity<SecurityEvent> update(@PathVariable Long id, @RequestBody SecurityEvent entity) {
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
    @AnonymousAccess
    public ResponseEntity<Void> deleteAll(@RequestBody Set<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.ok().build();
    }
} 