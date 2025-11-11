package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.AttackStats;
import me.zhengjie.modules.stat.service.AttackStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/attackStats")
public class AttackStatsController {

    @Autowired
    private AttackStatsService service;

    /**
     * 获取所有恶意IP攻击统计
     */
    @GetMapping
    public List<AttackStats> getAll() {
        return service.getAll();
    }

    /**
     * 按ID查询
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttackStats> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * 按统计日期查询
     */
    @GetMapping("/date/{statDate}")
    public ResponseEntity<AttackStats> getByStatDate(@PathVariable LocalDateTime statDate) {
        return service.findByStatDate(statDate)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 创建新统计记录
     */
    @PostMapping
    public ResponseEntity<AttackStats> create(@RequestBody AttackStats entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    /**
     * 更新统计记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttackStats> update(@PathVariable Long id, @RequestBody AttackStats entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    /**
     * 按ID删除
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@RequestBody Set<Long> ids) {
        service.deleteAll(ids);
        return ResponseEntity.ok().build();
    }
}