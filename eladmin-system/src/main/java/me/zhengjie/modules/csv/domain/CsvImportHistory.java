package me.zhengjie.modules.csv.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "csv_import_history")
public class CsvImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String importType;
    private Integer importedCount;
    private Integer failedCount;
    private LocalDateTime importTime;
    private String status;
    private String errorMessage;
    private String createdBy;
    private LocalDateTime createdTime;
} 