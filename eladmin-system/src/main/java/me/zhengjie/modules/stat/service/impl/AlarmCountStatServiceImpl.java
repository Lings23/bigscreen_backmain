package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AlarmCountStat;
import me.zhengjie.modules.stat.repository.AlarmCountStatRepository;
import me.zhengjie.modules.stat.service.AlarmCountStatService;
import org.springframework.beans.factory.annotation.Autowired;
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
} 