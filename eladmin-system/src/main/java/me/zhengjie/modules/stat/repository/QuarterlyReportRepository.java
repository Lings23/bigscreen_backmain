package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.QuarterlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuarterlyReportRepository extends JpaRepository<QuarterlyReport, Long> {
    // 新增按季度查询的方法（避免重复添加同季度报告）
    Optional<QuarterlyReport> findByReportQuarter(String reportQuarter);
}