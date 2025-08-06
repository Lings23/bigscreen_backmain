package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.DutySchedule;
import me.zhengjie.modules.stat.repository.DutyScheduleRepository;
import me.zhengjie.modules.stat.service.DutyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class DutyScheduleServiceImpl extends BaseStatServiceImpl<DutySchedule, DutyScheduleRepository> implements DutyScheduleService {
    
    @Autowired
    public DutyScheduleServiceImpl(DutyScheduleRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "dutyDate";
    }

    @Override
    protected void setCreateTime(DutySchedule entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(DutySchedule entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(DutySchedule target, DutySchedule source) {
        if (source.getOrgName() != null) {
            target.setOrgName(source.getOrgName());
        }
        if (source.getLeaderName() != null) {
            target.setLeaderName(source.getLeaderName());
        }
        if (source.getLeaderPhone() != null) {
            target.setLeaderPhone(source.getLeaderPhone());
        }
        if (source.getDutyPerson() != null) {
            target.setDutyPerson(source.getDutyPerson());
        }
        if (source.getDutyPhone() != null) {
            target.setDutyPhone(source.getDutyPhone());
        }
        if (source.getDutyDate() != null) {
            target.setDutyDate(source.getDutyDate());
        }
    }
} 