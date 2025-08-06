package me.zhengjie.modules.stat.rest;

import me.zhengjie.modules.stat.domain.DeviceStat;
import me.zhengjie.modules.stat.service.DeviceStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stat/device")
public class DeviceStatController {
    @Autowired
    private DeviceStatService service;

    @GetMapping
    public ResponseEntity<List<DeviceStat>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceStat> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/range")
    public ResponseEntity<List<DeviceStat>> getByTimeRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(service.getByTimeRange(startTime, endTime));
    }

    @PostMapping
    public ResponseEntity<DeviceStat> create(@RequestBody DeviceStat stat) {
        return new ResponseEntity<>(service.save(stat), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DeviceStat> update(@PathVariable Long id, @RequestBody DeviceStat stat) {
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