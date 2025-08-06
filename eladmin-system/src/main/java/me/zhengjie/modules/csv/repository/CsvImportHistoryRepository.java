package me.zhengjie.modules.csv.repository;

import me.zhengjie.modules.csv.domain.CsvImportHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CsvImportHistoryRepository extends JpaRepository<CsvImportHistory, Long> {
} 