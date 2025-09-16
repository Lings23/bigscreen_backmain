package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.DeviceStat;
import me.zhengjie.modules.stat.dto.DeviceStatQueryCriteria;
import me.zhengjie.modules.stat.repository.DeviceStatRepository;
import me.zhengjie.modules.stat.service.DeviceStatService;
import me.zhengjie.modules.stat.specification.DeviceStatSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    protected Page<DeviceStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<DeviceStat> findByKeyField(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // For DeviceStat, search by numeric values in counts
        try {
            Integer numericValue = Integer.parseInt(keyword);
            return repository.findByOnlineCountOrOfflineCountOrAlarmCount(numericValue, numericValue, numericValue, pageable);
        } catch (Exception e) {
            return repository.findAll(pageable);
        }
    }

    /**
     * 复杂条件查询
     */
    public Page<DeviceStat> findByCriteria(DeviceStatQueryCriteria criteria, Pageable pageable) {
        Specification<DeviceStat> spec = DeviceStatSpecification.build(criteria);
        return repository.findAll(spec, pageable);
    }
} 