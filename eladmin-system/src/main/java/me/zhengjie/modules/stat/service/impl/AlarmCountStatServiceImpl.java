package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AlarmCountStat;
import me.zhengjie.modules.stat.dto.AlarmCountStatQueryCriteria;
import me.zhengjie.modules.stat.repository.AlarmCountStatRepository;
import me.zhengjie.modules.stat.service.AlarmCountStatService;
import me.zhengjie.modules.stat.specification.AlarmCountStatSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AlarmCountStatServiceImpl extends BaseStatServiceImpl<AlarmCountStat, AlarmCountStatRepository> implements AlarmCountStatService {
    
    @Autowired
    public AlarmCountStatServiceImpl(AlarmCountStatRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "statDate";
    }

    @Override
    protected void setCreateTime(AlarmCountStat entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(AlarmCountStat entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(AlarmCountStat target, AlarmCountStat source) {
        if (source.getStatDate() != null) {
            target.setStatDate(source.getStatDate());
        }
        if (source.getSuccessCount() != null) {
            target.setSuccessCount(source.getSuccessCount());
        }
        if (source.getSuspiciousCount() != null) {
            target.setSuspiciousCount(source.getSuspiciousCount());
        }
        if (source.getAttemptCount() != null) {
            target.setAttemptCount(source.getAttemptCount());
        }
    }

    @Override
    protected Page<AlarmCountStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<AlarmCountStat> findByKeyField(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // For AlarmCountStat, search by stat date as the key field
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(keyword);
            return repository.findByStatDate(date, pageable);
        } catch (Exception e) {
            return repository.findAll(pageable);
        }
    }

    /**
     * 复杂条件查询
     */
    public Page<AlarmCountStat> findByCriteria(AlarmCountStatQueryCriteria criteria, Pageable pageable) {
        Specification<AlarmCountStat> spec = AlarmCountStatSpecification.build(criteria);
        return repository.findAll(spec, pageable);
    }
} 