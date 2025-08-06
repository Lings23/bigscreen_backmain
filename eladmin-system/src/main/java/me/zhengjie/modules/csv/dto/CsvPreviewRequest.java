package me.zhengjie.modules.csv.dto;

import lombok.Data;

@Data
public class CsvPreviewRequest {
    private String fileName;
    private int previewRows;
} 