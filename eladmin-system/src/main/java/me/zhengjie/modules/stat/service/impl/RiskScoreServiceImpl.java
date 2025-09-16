package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.RiskScore;
import me.zhengjie.modules.stat.dto.RiskScoreQueryCriteria;
import me.zhengjie.modules.stat.repository.RiskScoreRepository;
import me.zhengjie.modules.stat.service.RiskScoreService;
import me.zhengjie.modules.stat.specification.RiskScoreSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    protected Page<RiskScore> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<RiskScore> findByKeyField(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // For RiskScore, search by system name
        return repository.findBySystemNameContainingIgnoreCase(keyword, pageable);
    }

    /**
     * 复杂条件查询
     */
    public Page<RiskScore> findByCriteria(RiskScoreQueryCriteria criteria, Pageable pageable) {
        Specification<RiskScore> spec = RiskScoreSpecification.build(criteria);
        return repository.findAll(spec, pageable);
    }
} 