package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.ReBaoEvent;
import me.zhengjie.modules.stat.repository.ReBaoEventRepository;
import me.zhengjie.modules.stat.service.ReBaoEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ReBaoEventServiceImpl extends BaseStatServiceImpl<ReBaoEvent, ReBaoEventRepository> implements ReBaoEventService {
    
    @Autowired
    public ReBaoEventServiceImpl(ReBaoEventRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "startTime";
    }

    @Override
    protected void setCreateTime(ReBaoEvent entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(ReBaoEvent entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(ReBaoEvent target, ReBaoEvent source) {
        if (source.getEventName() != null) {
            target.setEventName(source.getEventName());
        }
        if (source.getParticipatingUnits() != null) {
            target.setParticipatingUnits(source.getParticipatingUnits());
        }
        if (source.getStartTime() != null) {
            target.setStartTime(source.getStartTime());
        }
        if (source.getEndTime() != null) {
            target.setEndTime(source.getEndTime());
        }
    }
}