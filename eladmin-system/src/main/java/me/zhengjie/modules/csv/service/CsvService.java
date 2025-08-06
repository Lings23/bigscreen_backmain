package me.zhengjie.modules.csv.service;

import me.zhengjie.modules.csv.dto.CsvPreviewRequest;
import me.zhengjie.modules.csv.dto.CsvImportRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface CsvService {
    Map<String, Object> uploadCsv(MultipartFile file);
    Map<String, Object> previewCsv(CsvPreviewRequest request);
    Map<String, Object> importCsv(CsvImportRequest request);
    Map<String, Object> getHistory(int page, int size);
    void downloadTemplate(HttpServletResponse response) throws IOException;
    Map<String, Object> getAllTables();
    Map<String, Object> createTable(Map<String, Object> request);
} 