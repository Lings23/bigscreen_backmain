package me.zhengjie.modules.csv.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class CsvImportRequest {
    private String fileName;
    private String importType;
    private boolean validateOnly;
    // 新增字段
    private String tableName;
    private List<Map<String, Object>> data;
} 