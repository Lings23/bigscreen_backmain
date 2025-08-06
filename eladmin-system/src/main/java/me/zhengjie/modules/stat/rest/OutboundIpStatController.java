package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.OutboundIpStat;
import me.zhengjie.modules.stat.service.OutboundIpStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/outbound-ip")
public class OutboundIpStatController {
    @Autowired
    private OutboundIpStatService service;

    @GetMapping
    public ResponseEntity<List<OutboundIpStat>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OutboundIpStat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<OutboundIpStat> create(@RequestBody OutboundIpStat stat) {
        return new ResponseEntity<>(service.save(stat), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<OutboundIpStat> update(@PathVariable Long id, @RequestBody OutboundIpStat stat) {
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