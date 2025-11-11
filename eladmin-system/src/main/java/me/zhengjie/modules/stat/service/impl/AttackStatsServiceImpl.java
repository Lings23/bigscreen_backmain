package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AttackStats;
import me.zhengjie.modules.stat.repository.AttackStatsRepository;
import me.zhengjie.modules.stat.service.AttackStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AttackStatsServiceImpl extends BaseStatServiceImpl<AttackStats, AttackStatsRepository> implements AttackStatsService {

    @Autowired
    public AttackStatsServiceImpl(AttackStatsRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "statDate"; // 默认按统计日期排序
    }

    @Override
    protected void setCreateTime(AttackStats entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(AttackStats entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(AttackStats target, AttackStats source) {
        // 仅更新非空字段
        if (source.getMaliciousCodeAttack() != null) {
            target.setMaliciousCodeAttack(source.getMaliciousCodeAttack());
        }
        if (source.getVulnerabilityAttack() != null) {
            target.setVulnerabilityAttack(source.getVulnerabilityAttack());
        }
        if (source.getDosAttack() != null) {
            target.setDosAttack(source.getDosAttack());
        }
        if (source.getScanProbe() != null) {
            target.setScanProbe(source.getScanProbe());
        }
        if (source.getOtherAttack() != null) {
            target.setOtherAttack(source.getOtherAttack());
        }
        if (source.getStatDate() != null) {
            target.setStatDate(source.getStatDate());
        }
    }

    // 实现按统计日期查询的方法
    @Override
    public Optional<AttackStats> findByStatDate(LocalDateTime statDate) {
        return repository.findByStatDate(statDate);
    }
}