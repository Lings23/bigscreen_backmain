package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AbnormalEventStat;
import me.zhengjie.modules.stat.repository.AbnormalEventStatRepository;
import me.zhengjie.modules.stat.service.AbnormalEventStatService;
import org.springframework.beans.factory.annotation.Autowired;
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
} 