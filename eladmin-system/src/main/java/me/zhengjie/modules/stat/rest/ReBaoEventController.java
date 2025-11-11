package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.ReBaoEvent;
import me.zhengjie.modules.stat.service.ReBaoEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/reBaoEvent")
public class ReBaoEventController {
    @Autowired
    private ReBaoEventService service;

    @GetMapping
    public List<ReBaoEvent> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReBaoEvent> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<ReBaoEvent> create(@RequestBody ReBaoEvent entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ReBaoEvent> update(@PathVariable Long id, @RequestBody ReBaoEvent entity) {
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
    public ResponseEntity<Void> deleteAll(@RequestBody Set<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.ok().build();
    }
}