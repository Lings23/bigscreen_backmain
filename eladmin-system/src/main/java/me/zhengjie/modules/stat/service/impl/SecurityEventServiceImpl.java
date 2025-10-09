package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.SecurityEvent;
import me.zhengjie.modules.stat.dto.SecurityEventQueryCriteria;
import me.zhengjie.modules.stat.repository.SecurityEventRepository;
import me.zhengjie.modules.stat.service.SecurityEventService;
import me.zhengjie.modules.stat.specification.SecurityEventSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public Page<SecurityEvent> findAll(Specification<SecurityEvent> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public Page<SecurityEvent> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size) {
        Pageable pageable = createPageable(page, size);
        return findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    public Page<SecurityEvent> findByKeyword(String keyword, Integer page, Integer size) {
        Pageable pageable = createPageable(page, size);
        return findByKeyField(keyword, pageable);
    }

    @Override
    public Page<SecurityEvent> findByCriteria(SecurityEventQueryCriteria criteria, Pageable pageable) {
        Specification<SecurityEvent> spec = SecurityEventSpecification.createSpecification(criteria);
        return findAll(spec, pageable);
    }

    @Override
    public Pageable createPageable(Integer page, Integer size) {
        return super.createPageable(page, size);
    }
} 