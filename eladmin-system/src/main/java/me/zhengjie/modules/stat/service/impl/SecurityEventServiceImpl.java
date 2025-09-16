package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.SecurityEvent;
import me.zhengjie.modules.stat.repository.SecurityEventRepository;
import me.zhengjie.modules.stat.service.SecurityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class SecurityEventServiceImpl extends BaseStatServiceImpl<SecurityEvent, SecurityEventRepository> implements SecurityEventService {
    
    @Autowired
    public SecurityEventServiceImpl(SecurityEventRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "eventTime";
    }

    @Override
    protected void setCreateTime(SecurityEvent entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(SecurityEvent entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(SecurityEvent target, SecurityEvent source) {
        if (source.getSystemName() != null) {
            target.setSystemName(source.getSystemName());
        }
        if (source.getIpAddress() != null) {
            target.setIpAddress(source.getIpAddress());
        }
        if (source.getEventTime() != null) {
            target.setEventTime(source.getEventTime());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        if (source.getSource() != null) {
            target.setSource(source.getSource());
        }
        if (source.getContent() != null) {
            target.setContent(source.getContent());
        }
    }

    @Override
    protected Page<SecurityEvent> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<SecurityEvent> findByKeyField(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // Search by system name as the primary key field for SecurityEvent
        return repository.findBySystemNameContainingIgnoreCase(keyword, pageable);
    }
} 