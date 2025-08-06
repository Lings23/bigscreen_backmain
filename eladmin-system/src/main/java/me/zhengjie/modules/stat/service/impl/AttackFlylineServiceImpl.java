package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AttackFlyline;
import me.zhengjie.modules.stat.repository.AttackFlylineRepository;
import me.zhengjie.modules.stat.service.AttackFlylineService;
import org.springframework.beans.factory.annotation.Autowired;
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
} 