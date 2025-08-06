package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AlertRegionStat;
import me.zhengjie.modules.stat.repository.AlertRegionStatRepository;
import me.zhengjie.modules.stat.service.AlertRegionStatService;
import org.springframework.beans.factory.annotation.Autowired;
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
} 