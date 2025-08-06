package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.AlertRegionStat;
import me.zhengjie.modules.stat.service.AlertRegionStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/region")
public class AlertRegionStatController {
    @Autowired
    private AlertRegionStatService service;

    @GetMapping
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
} 