package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AlertRegionStat;
import me.zhengjie.modules.stat.dto.AlertRegionStatQueryCriteria;
import me.zhengjie.modules.stat.repository.AlertRegionStatRepository;
import me.zhengjie.modules.stat.service.AlertRegionStatService;
import me.zhengjie.modules.stat.specification.AlertRegionStatSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AlertRegionStatServiceImpl extends BaseStatServiceImpl<AlertRegionStat, AlertRegionStatRepository> implements AlertRegionStatService {
    
    @Autowired
    public AlertRegionStatServiceImpl(AlertRegionStatRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "statDate";
    }

    @Override
    protected void setCreateTime(AlertRegionStat entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(AlertRegionStat entity) {
        // AlertRegionStat 没有 updatedAt 字段，跳过
    }

    @Override
    protected void updateFields(AlertRegionStat target, AlertRegionStat source) {
        if (source.getRegionName() != null) {
            target.setRegionName(source.getRegionName());
        }
        if (source.getRegionType() != null) {
            target.setRegionType(source.getRegionType());
        }
        if (source.getAlertCount() != null) {
            target.setAlertCount(source.getAlertCount());
        }
        if (source.getStatDate() != null) {
            target.setStatDate(source.getStatDate());
        }
    }

    @Override
    protected Page<AlertRegionStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<AlertRegionStat> findByKeyField(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // For AlertRegionStat, search by region name as the key field
        return repository.findByRegionNameContainingIgnoreCase(keyword, pageable);
    }

    /**
     * 复杂条件查询
     */
    public Page<AlertRegionStat> findByCriteria(AlertRegionStatQueryCriteria criteria, Pageable pageable) {
        Specification<AlertRegionStat> spec = AlertRegionStatSpecification.build(criteria);
        return repository.findAll(spec, pageable);
    }
} 