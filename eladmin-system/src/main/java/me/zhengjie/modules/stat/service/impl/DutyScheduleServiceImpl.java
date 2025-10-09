package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.DutySchedule;
import me.zhengjie.modules.stat.dto.DutyScheduleQueryCriteria;
import me.zhengjie.modules.stat.repository.DutyScheduleRepository;
import me.zhengjie.modules.stat.service.DutyScheduleService;
import me.zhengjie.modules.stat.specification.DutyScheduleSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        if (source.getEventName() != null) {
            target.setEventName(source.getEventName());
        }
    }

    @Override
    protected Page<DutySchedule> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<DutySchedule> findByKeyField(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // For DutySchedule, search by organization name, leader name, or duty person
        return repository.findByOrgNameContainingIgnoreCaseOrLeaderNameContainingIgnoreCaseOrDutyPersonContainingIgnoreCase(
            keyword, keyword, keyword, pageable);
    }

    @Override
    public Page<DutySchedule> findAll(Specification<DutySchedule> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public Page<DutySchedule> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size) {
        Pageable pageable = createPageable(page, size);
        return findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    public Page<DutySchedule> findByKeyword(String keyword, Integer page, Integer size) {
        Pageable pageable = createPageable(page, size);
        return findByKeyField(keyword, pageable);
    }

    @Override
    public Page<DutySchedule> findByCriteria(DutyScheduleQueryCriteria criteria, Pageable pageable) {
        Specification<DutySchedule> spec = DutyScheduleSpecification.build(criteria);
        return findAll(spec, pageable);
    }

    @Override
    public Pageable createPageable(Integer page, Integer size) {
        return super.createPageable(page, size);
    }
} 