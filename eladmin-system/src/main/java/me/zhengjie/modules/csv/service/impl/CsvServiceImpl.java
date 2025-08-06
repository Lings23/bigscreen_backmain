package me.zhengjie.modules.csv.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.csv.domain.CsvImportHistory;
import me.zhengjie.modules.csv.dto.CsvImportRequest;
import me.zhengjie.modules.csv.dto.CsvPreviewRequest;
import me.zhengjie.modules.csv.repository.CsvImportHistoryRepository;
import me.zhengjie.modules.csv.service.CsvService;
import me.zhengjie.modules.csv.util.CsvUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvServiceImpl implements CsvService {

    private final CsvImportHistoryRepository historyRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final DataSource dataSource;

    @Value("${csv.upload-dir:csv_uploads}")
    private String uploadDir;

    @Override
    public Map<String, Object> uploadCsv(MultipartFile file) {
        if (file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "文件格式不支持");
            result.put("data", Collections.emptyMap());
            return result;
        }
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir, fileName);
            file.transferTo(dest);
            Map<String, Object> data = new HashMap<>();
            data.put("fileName", fileName);
            data.put("fileSize", file.getSize());
            data.put("uploadTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "上传成功");
            result.put("data", data);
            return result;
        } catch (IOException e) {
            log.error("CSV上传失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "上传失败");
            result.put("data", Collections.emptyMap());
            return result;
        }
    }

    @Override
    public Map<String, Object> previewCsv(CsvPreviewRequest request) {
        File file = new File(uploadDir, request.getFileName());
        if (!file.exists()) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 404);
            result.put("message", "文件不存在");
            result.put("data", Collections.emptyMap());
            return result;
        }
        try {
            List<String> headers = CsvUtils.getHeaders(file);
            List<Map<String, String>> previewData = CsvUtils.previewRows(file, request.getPreviewRows());
            int totalRows = CsvUtils.countRows(file);
            Map<String, Object> data = new HashMap<>();
            data.put("headers", headers);
            data.put("previewData", previewData);
            data.put("totalRows", totalRows);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "预览成功");
            result.put("data", data);
            return result;
        } catch (IOException e) {
            log.error("CSV预览失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "预览失败");
            result.put("data", Collections.emptyMap());
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> importCsv(CsvImportRequest request) {
        // 新版：支持指定表名和数据导入
        String tableName = request.getTableName();
        List<Map<String, Object>> data = request.getData();
        if (tableName == null || data == null || data.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "参数错误");
            result.put("data", Collections.emptyMap());
            return result;
        }
        int importedCount = 0, failedCount = 0;
        List<String> errors = new ArrayList<>();
        String status = "SUCCESS";
        StringBuilder sql = new StringBuilder();
        try {
            // 过滤掉id、created_at、updated_at字段，这些字段由数据库自动处理
            List<String> excludeFields = Arrays.asList("id", "created_at", "updated_at");
            List<String> columns = new ArrayList<>();
            for (String col : data.get(0).keySet()) {
                // 过滤掉空列名、无效列名和系统字段
                if (col != null && !col.trim().isEmpty() && 
                    !excludeFields.contains(col.toLowerCase()) &&
                    !col.matches("^_\\d+$") && // 过滤掉类似 _1, _2 的列名
                    !col.matches("^\\s*$")) { // 过滤掉只包含空格的列名
                    columns.add(col.trim());
                }
            }
            
            // 添加时间字段到列名中
            List<String> allColumns = new ArrayList<>(columns);
            allColumns.add("created_at");
            allColumns.add("updated_at");
            
            // 确保列名是有效的SQL标识符
            List<String> quotedColumns = new ArrayList<>();
            for (String col : allColumns) {
                quotedColumns.add("`" + col + "`");
            }
            String colStr = String.join(", ", quotedColumns);
            sql.append("INSERT INTO `").append(tableName).append("` (").append(colStr).append(") VALUES ");
            
            List<String> valueRows = new ArrayList<>();
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            for (Map<String, Object> row : data) {
                List<String> values = new ArrayList<>();
                // 添加CSV数据中的值
                for (String col : columns) {
                    Object v = row.get(col);
                    if (v == null || v.toString().trim().isEmpty()) {
                        values.add("NULL");
                    } else {
                        String value = v.toString().trim();
                        // 处理特殊字符转义
                        value = value.replace("'", "''");
                        
                        // 检查是否是日期时间格式并转换
                        String convertedValue = convertDateTimeFormat(value);
                        
                        // 如果是数字类型，不需要加引号
                        if (convertedValue.matches("^-?\\d+(\\.\\d+)?$")) {
                            values.add(convertedValue);
                        } else {
                            values.add("'" + convertedValue + "'");
                        }
                    }
                }
                // 添加时间字段的默认值
                values.add("'" + currentTime + "'");
                values.add("'" + currentTime + "'");
                valueRows.add("(" + String.join(", ", values) + ")");
            }
            sql.append(String.join(", ", valueRows));
            entityManager.createNativeQuery(sql.toString()).executeUpdate();
            importedCount = data.size();
            
            // 数据导入成功后，保存导入历史
            saveImportHistory(request, importedCount, 0, "SUCCESS", Collections.emptyList());
            
        } catch (Exception e) {
            log.error("CSV导入失败，表名: {}", tableName);
            log.error("完整SQL语句: {}", sql != null ? sql.toString() : "[SQL未生成]");
            log.error("异常详情", e);
            status = "FAILED";
            errors.add(e.getMessage() + (e.getCause() != null ? ("; cause: " + e.getCause().getMessage()) : ""));
            failedCount = data != null ? data.size() : 0;
            
            // 导入失败，保存失败历史记录
            saveImportHistory(request, 0, failedCount, "FAILED", errors);
            
            // 抛出异常，让Spring事务管理器正确回滚
            throw new RuntimeException("CSV导入失败: " + e.getMessage(), e);
        }
        
        Map<String, Object> respData = new HashMap<>();
        respData.put("importedCount", importedCount);
        respData.put("failedCount", failedCount);
        respData.put("errors", errors);
        respData.put("importTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "导入成功");
        result.put("data", respData);
        return result;
    }
    
    /**
     * 使用单独的事务保存导入历史记录
     * 无论主事务是否回滚，历史记录都会保存
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveImportHistory(CsvImportRequest request, int importedCount, int failedCount, String status, List<String> errors) {
        CsvImportHistory history = new CsvImportHistory();
        history.setFileName(request.getFileName() != null ? request.getFileName() : request.getTableName());
        history.setImportType(request.getImportType() != null ? request.getImportType() : request.getTableName());
        history.setImportedCount(importedCount);
        history.setFailedCount(failedCount);
        history.setImportTime(LocalDateTime.now());
        history.setStatus(status);
        history.setErrorMessage(String.join("; ", errors));
        history.setCreatedBy("system");
        history.setCreatedTime(LocalDateTime.now());
        historyRepository.save(history);
    }

    /**
     * 转换日期时间格式
     * 将常见的日期时间格式转换为MySQL兼容的格式
     */
    private String convertDateTimeFormat(String value) {
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        
        // 尝试匹配 DD/M/YYYY HH:mm:ss 格式 (如: 22/7/2025 10:55:29)
        if (value.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{2}:\\d{2}")) {
            try {
                java.time.format.DateTimeFormatter inputFormatter = 
                    java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy HH:mm:ss");
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(value, inputFormatter);
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                log.warn("日期格式转换失败: {}", value, e);
            }
        }
        
        // 尝试匹配 DD/MM/YYYY HH:mm:ss 格式 (如: 22/07/2025 10:55:29)
        if (value.matches("\\d{2}/\\d{2}/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2}")) {
            try {
                java.time.format.DateTimeFormatter inputFormatter = 
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(value, inputFormatter);
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                log.warn("日期格式转换失败: {}", value, e);
            }
        }
        
        // 尝试匹配 MM/DD/YYYY HH:mm:ss 格式 (如: 07/22/2025 10:55:29)
        if (value.matches("\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{2}:\\d{2}")) {
            try {
                java.time.format.DateTimeFormatter inputFormatter = 
                    java.time.format.DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss");
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(value, inputFormatter);
                return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                log.warn("日期格式转换失败: {}", value, e);
            }
        }
        
        // 如果已经是标准格式，直接返回
        if (value.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}")) {
            return value;
        }
        
        // 其他格式保持不变
        return value;
    }

    @Override
    public Map<String, Object> getHistory(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1, size, org.springframework.data.domain.Sort.by("importTime").descending());
        org.springframework.data.domain.Page<me.zhengjie.modules.csv.domain.CsvImportHistory> pageResult = historyRepository.findAll(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("content", pageResult.getContent());
        data.put("totalElements", pageResult.getTotalElements());
        data.put("totalPages", pageResult.getTotalPages());
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", data);
        return result;
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        String template = "姓名,年龄,邮箱,部门,职位\n张三,25,zhangsan@example.com,技术部,开发工程师\n";
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=template.csv");
        response.getWriter().write(template);
        response.getWriter().flush();
    }

    @Override
    public Map<String, Object> getAllTables() {
        List<String> tables = new ArrayList<>();
        try {
            try (java.sql.Connection conn = dataSource.getConnection()) {
                java.sql.DatabaseMetaData metaData = conn.getMetaData();
                try (java.sql.ResultSet rs = metaData.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"})) {
                    while (rs.next()) {
                        tables.add(rs.getString("TABLE_NAME"));
                    }
                }
            }
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "查询成功");
            result.put("data", tables);
            return result;
        } catch (Exception e) {
            log.error("获取表名失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "获取表名失败");
            result.put("data", Collections.emptyMap());
            return result;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> createTable(Map<String, Object> request) {
        String tableName = (String) request.get("tableName");
        List<Map<String, String>> columns = (List<Map<String, String>>) request.get("columns");
        if (tableName == null || columns == null || columns.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 400);
            result.put("message", "参数错误");
            result.put("data", Collections.emptyMap());
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
        List<String> colDefs = new ArrayList<>();
        for (Map<String, String> col : columns) {
            String name = col.get("name");
            String type = col.get("type");
            if (name == null || type == null) continue;
            colDefs.add("`" + name + "` " + type);
        }
        sb.append(String.join(", ", colDefs));
        sb.append(")");
        try {
            entityManager.createNativeQuery(sb.toString()).executeUpdate();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "创建成功");
            result.put("data", Collections.emptyMap());
            return result;
        } catch (Exception e) {
            log.error("建表失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("code", 500);
            result.put("message", "建表失败: " + e.getMessage());
            result.put("data", Collections.emptyMap());
            return result;
        }
    }
} 