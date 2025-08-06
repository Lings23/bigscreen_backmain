package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.AssetStat;
import me.zhengjie.modules.stat.service.AssetStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/asset")
public class AssetStatController {
    @Autowired
    private AssetStatService service;

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
} 