package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.QuarterlyReport;
import me.zhengjie.modules.stat.repository.QuarterlyReportRepository;
import me.zhengjie.modules.stat.service.QuarterlyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class QuarterlyReportServiceImpl extends BaseStatServiceImpl<QuarterlyReport, QuarterlyReportRepository> implements QuarterlyReportService {

    @Autowired
    public QuarterlyReportServiceImpl(QuarterlyReportRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "reportQuarter"; // 默认按季度排序
    }

    @Override
    protected void setCreateTime(QuarterlyReport entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(QuarterlyReport entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(QuarterlyReport target, QuarterlyReport source) {
        // 仅更新非空字段
        if (source.getReportQuarter() != null) {
            target.setReportQuarter(source.getReportQuarter());
        }
        if (source.getSecurityRetrievalCount() != null) {
            target.setSecurityRetrievalCount(source.getSecurityRetrievalCount());
        }
        if (source.getSecurityTrainingCount() != null) {
            target.setSecurityTrainingCount(source.getSecurityTrainingCount());
        }
        if (source.getSystemRectificationCount() != null) {
            target.setSystemRectificationCount(source.getSystemRectificationCount());
        }
    }

    // 实现按季度查询的方法
    @Override
    public Optional<QuarterlyReport> findByReportQuarter(String reportQuarter) {
        return repository.findByReportQuarter(reportQuarter);
    }
}