package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.OutboundIpStat;
import me.zhengjie.modules.stat.repository.OutboundIpStatRepository;
import me.zhengjie.modules.stat.service.OutboundIpStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class OutboundIpStatServiceImpl extends BaseStatServiceImpl<OutboundIpStat, OutboundIpStatRepository> implements OutboundIpStatService {
    
    @Autowired
    public OutboundIpStatServiceImpl(OutboundIpStatRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "statTime";
    }

    @Override
    protected void setCreateTime(OutboundIpStat entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(OutboundIpStat entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(OutboundIpStat target, OutboundIpStat source) {
        if (source.getStatTime() != null) {
            target.setStatTime(source.getStatTime());
        }
        if (source.getLocation() != null) {
            target.setLocation(source.getLocation());
        }
        if (source.getIsDomestic() != null) {
            target.setIsDomestic(source.getIsDomestic());
        }
        if (source.getIpCount() != null) {
            target.setIpCount(source.getIpCount());
        }
    }
} 