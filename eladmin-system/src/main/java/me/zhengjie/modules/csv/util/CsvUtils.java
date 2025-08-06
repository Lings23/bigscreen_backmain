package me.zhengjie.modules.csv.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Arrays;

public class CsvUtils {
    public static List<Map<String, String>> parseCsv(File file) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            for (CSVRecord record : parser) {
                Map<String, String> row = new LinkedHashMap<>();
                for (String header : parser.getHeaderMap().keySet()) {
                    row.put(header, record.get(header));
                }
                data.add(row);
            }
        }
        return data;
    }

    public static List<String> getHeaders(File file) throws IOException {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());
            // 过滤掉id、created_at、updated_at字段，这些字段由数据库自动处理
            List<String> excludeFields = Arrays.asList("id", "created_at", "updated_at");
            headers.removeIf(header -> 
                header == null || 
                header.trim().isEmpty() || 
                excludeFields.contains(header.toLowerCase()) ||
                header.matches("^_\\d+$") || // 过滤掉类似 _1, _2 的列名
                header.matches("^\\s*$") // 过滤掉只包含空格的列名
            );
            return headers;
        }
    }

    public static int countRows(File file) throws IOException {
        int count = 0;
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            for (CSVRecord ignored : parser) {
                count++;
            }
        }
        return count;
    }

    public static List<Map<String, String>> previewRows(File file, int previewRows) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)) {
            int i = 0;
            // 过滤掉id、created_at、updated_at字段，这些字段由数据库自动处理
            List<String> excludeFields = Arrays.asList("id", "created_at", "updated_at");
            List<String> headers = new ArrayList<>(parser.getHeaderMap().keySet());
            headers.removeIf(header -> 
                header == null || 
                header.trim().isEmpty() || 
                excludeFields.contains(header.toLowerCase()) ||
                header.matches("^_\\d+$") || // 过滤掉类似 _1, _2 的列名
                header.matches("^\\s*$") // 过滤掉只包含空格的列名
            );
            
            for (CSVRecord record : parser) {
                if (i++ >= previewRows) break;
                Map<String, String> row = new LinkedHashMap<>();
                for (String header : headers) {
                    row.put(header, record.get(header));
                }
                data.add(row);
            }
        }
        return data;
    }
} 