package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.AlarmCountStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public interface AlarmCountStatService extends BaseStatService<AlarmCountStat> {
    
    /**
     * 标准CRUD查询 - 支持动态Specification和Pageable
     */
    Page<AlarmCountStat> findAll(Specification<AlarmCountStat> spec, Pageable pageable);

} 