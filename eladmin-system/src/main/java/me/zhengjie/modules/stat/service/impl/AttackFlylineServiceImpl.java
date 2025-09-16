package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AttackFlyline;
import me.zhengjie.modules.stat.dto.AttackFlylineQueryCriteria;
import me.zhengjie.modules.stat.repository.AttackFlylineRepository;
import me.zhengjie.modules.stat.service.AttackFlylineService;
import me.zhengjie.modules.stat.specification.AttackFlylineSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AttackFlylineServiceImpl extends BaseStatServiceImpl<AttackFlyline, AttackFlylineRepository> implements AttackFlylineService {
    
    @Autowired
    public AttackFlylineServiceImpl(AttackFlylineRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "attackTime";
    }

    @Override
    protected void setCreateTime(AttackFlyline entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(AttackFlyline entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(AttackFlyline target, AttackFlyline source) {
        if (source.getSourceIp() != null) {
            target.setSourceIp(source.getSourceIp());
        }
        if (source.getTargetIp() != null) {
            target.setTargetIp(source.getTargetIp());
        }
        if (source.getSourceLocationName() != null) {
            target.setSourceLocationName(source.getSourceLocationName());
        }
        if (source.getSourceLng() != null) {
            target.setSourceLng(source.getSourceLng());
        }
        if (source.getSourceLat() != null) {
            target.setSourceLat(source.getSourceLat());
        }
        if (source.getTargetLocationName() != null) {
            target.setTargetLocationName(source.getTargetLocationName());
        }
        if (source.getTargetLng() != null) {
            target.setTargetLng(source.getTargetLng());
        }
        if (source.getTargetLat() != null) {
            target.setTargetLat(source.getTargetLat());
        }
        if (source.getAttackMethod() != null) {
            target.setAttackMethod(source.getAttackMethod());
        }
        if (source.getTargetSystem() != null) {
            target.setTargetSystem(source.getTargetSystem());
        }
        if (source.getAttackTime() != null) {
            target.setAttackTime(source.getAttackTime());
        }
        if (source.getSourceIsDomestic() != null) {
            target.setSourceIsDomestic(source.getSourceIsDomestic());
        }
    }

    @Override
    protected Page<AttackFlyline> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<AttackFlyline> findByKeyField(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // For AttackFlyline, search by source IP, target IP, or attack method
        return repository.findBySourceIpContainingIgnoreCaseOrTargetIpContainingIgnoreCaseOrAttackMethodContainingIgnoreCase(
            keyword, keyword, keyword, pageable);
    }

    /**
     * 复杂条件查询
     */
    public Page<AttackFlyline> findByCriteria(AttackFlylineQueryCriteria criteria, Pageable pageable) {
        Specification<AttackFlyline> spec = AttackFlylineSpecification.build(criteria);
        return repository.findAll(spec, pageable);
    }
} 