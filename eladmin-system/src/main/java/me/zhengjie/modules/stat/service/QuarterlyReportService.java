package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.QuarterlyReport;
import java.util.Optional;

public interface QuarterlyReportService extends BaseStatService<QuarterlyReport> {
    // 扩展按季度查询的方法
    Optional<QuarterlyReport> findByReportQuarter(String reportQuarter);
}