package me.zhengjie.modules.stat.rest;

import me.zhengjie.annotation.rest.AnonymousAccess;
import me.zhengjie.modules.stat.domain.DutySchedule;
import me.zhengjie.modules.stat.service.DutyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/duty")
public class DutyScheduleController {
    @Autowired
    private DutyScheduleService service;

    @GetMapping
    
    public List<DutySchedule> getAll() {
        return service.getAll();
    }
    
    @GetMapping("/{id}")
    
    public ResponseEntity<DutySchedule> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    
    @PostMapping
    
    public ResponseEntity<DutySchedule> create(@RequestBody DutySchedule entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    
    public ResponseEntity<DutySchedule> update(@PathVariable Long id, @RequestBody DutySchedule entity) {
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