package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.DeviceStat;
import me.zhengjie.modules.stat.repository.DeviceStatRepository;
import me.zhengjie.modules.stat.service.DeviceStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceStatServiceImpl extends BaseStatServiceImpl<DeviceStat, DeviceStatRepository> implements DeviceStatService {
    
    @Autowired
    public DeviceStatServiceImpl(DeviceStatRepository repository) {
        super(repository);
    }

    @Override
    public List<DeviceStat> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return repository.findByStatTimeBetween(
            startTime,
            endTime,
            Sort.by(Sort.Direction.DESC, "statTime")
        );
    }

    @Override
    protected String getDefaultSortField() {
        return "statTime";
    }

    @Override
    protected void setCreateTime(DeviceStat entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(DeviceStat entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(DeviceStat target, DeviceStat source) {
        if (source.getStatTime() != null) {
            target.setStatTime(source.getStatTime());
        }
        if (source.getOnlineCount() != null) {
            target.setOnlineCount(source.getOnlineCount());
        }
        if (source.getOfflineCount() != null) {
            target.setOfflineCount(source.getOfflineCount());
        }
        if (source.getAlarmCount() != null) {
            target.setAlarmCount(source.getAlarmCount());
        }
    }
} 