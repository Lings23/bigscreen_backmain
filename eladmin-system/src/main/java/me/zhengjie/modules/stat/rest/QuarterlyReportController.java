package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.QuarterlyReport;
import me.zhengjie.modules.stat.service.QuarterlyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/quarterlyReport")
public class QuarterlyReportController {

    @Autowired
    private QuarterlyReportService service;

    /**
     * 获取所有安全季报
     */
    @GetMapping
    public List<QuarterlyReport> getAll() {
        return service.getAll();
    }

    /**
     * 按ID查询
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuarterlyReport> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * 按季度查询（如"2024-Q1"）
     */
    @GetMapping("/quarter/{quarter}")
    public ResponseEntity<QuarterlyReport> getByQuarter(@PathVariable String quarter) {
        return service.findByReportQuarter(quarter)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 创建新季报
     */
    @PostMapping
    public ResponseEntity<QuarterlyReport> create(@RequestBody QuarterlyReport entity) {
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    /**
     * 更新季报
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuarterlyReport> update(@PathVariable Long id, @RequestBody QuarterlyReport entity) {
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