package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.RiskScore;
import me.zhengjie.modules.stat.repository.RiskScoreRepository;
import me.zhengjie.modules.stat.service.RiskScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class RiskScoreServiceImpl extends BaseStatServiceImpl<RiskScore, RiskScoreRepository> implements RiskScoreService {
    
    @Autowired
    public RiskScoreServiceImpl(RiskScoreRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "scoreDate";
    }

    @Override
    protected void setCreateTime(RiskScore entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(RiskScore entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(RiskScore target, RiskScore source) {
        if (source.getSystemName() != null) {
            target.setSystemName(source.getSystemName());
        }
        if (source.getRiskScore() != null) {
            target.setRiskScore(source.getRiskScore());
        }
        if (source.getScoreDate() != null) {
            target.setScoreDate(source.getScoreDate());
        }
    }
} 