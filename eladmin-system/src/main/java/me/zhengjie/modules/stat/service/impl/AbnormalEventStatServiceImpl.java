package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AbnormalEventStat;
import me.zhengjie.modules.stat.dto.AbnormalEventStatQueryCriteria;
import me.zhengjie.modules.stat.repository.AbnormalEventStatRepository;
import me.zhengjie.modules.stat.service.AbnormalEventStatService;
import me.zhengjie.modules.stat.specification.AbnormalEventStatSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AbnormalEventStatServiceImpl extends BaseStatServiceImpl<AbnormalEventStat, AbnormalEventStatRepository> implements AbnormalEventStatService {
    
    @Autowired
    public AbnormalEventStatServiceImpl(AbnormalEventStatRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "statTime";
    }

    @Override
    protected void setCreateTime(AbnormalEventStat entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(AbnormalEventStat entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(AbnormalEventStat target, AbnormalEventStat source) {
        if (source.getStatTime() != null) {
            target.setStatTime(source.getStatTime());
        }
        if (source.getOutbound() != null) {
            target.setOutbound(source.getOutbound());
        }
        if (source.getOutsideToInside() != null) {
            target.setOutsideToInside(source.getOutsideToInside());
        }
        if (source.getLateralMove() != null) {
            target.setLateralMove(source.getLateralMove());
        }
        if (source.getOther() != null) {
            target.setOther(source.getOther());
        }
    }

    @Override
    protected Page<AbnormalEventStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<AbnormalEventStat> findByKeyField(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // For AbnormalEventStat, search by event type as the key field
        return repository.findAll(pageable);
    }

    /**
     * 复杂条件查询
     */
    public Page<AbnormalEventStat> findByCriteria(AbnormalEventStatQueryCriteria criteria, Pageable pageable) {
        Specification<AbnormalEventStat> spec = AbnormalEventStatSpecification.build(criteria);
        return repository.findAll(spec, pageable);
    }
    
    /**
     * 标准CRUD查询 - 支持动态Specification和Pageable
     */
    public Page<AbnormalEventStat> findAll(Specification<AbnormalEventStat> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }
}