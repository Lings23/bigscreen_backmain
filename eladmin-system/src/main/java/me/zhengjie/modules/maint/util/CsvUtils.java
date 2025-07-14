/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.maint.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV文件处理工具类
 * @author lc
 * @date 2025-07-14
 */
@Slf4j
public class CsvUtils {

    /**
     * 读取CSV文件内容
     * @param file CSV文件
     * @return 文件内容列表，每行作为一个元素
     */
    public static List<String> readCsvFile(MultipartFile file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            log.error("读取CSV文件失败: {}", e.getMessage());
            throw new RuntimeException("读取CSV文件失败: " + e.getMessage());
        }
        return lines;
    }

    /**
     * 解析CSV行数据
     * @param line CSV行数据
     * @return 解析后的字段列表
     */
    public static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // 处理双引号转义
                    field.append('"');
                    i++; // 跳过下一个引号
                } else {
                    // 切换引号状态
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // 遇到逗号且不在引号内，添加字段
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                // 添加字符到当前字段
                field.append(c);
            }
        }
        
        // 添加最后一个字段
        fields.add(field.toString().trim());
        
        return fields;
    }

    /**
     * 解析CSV文件为二维数组
     * @param file CSV文件
     * @return 二维数组，第一行通常是表头
     */
    public static List<List<String>> parseCsvFile(MultipartFile file) {
        List<String> lines = readCsvFile(file);
        List<List<String>> result = new ArrayList<>();
        
        for (String line : lines) {
            List<String> fields = parseCsvLine(line);
            result.add(fields);
        }
        
        return result;
    }

    /**
     * 验证CSV文件格式
     * @param file CSV文件
     * @return 是否有效
     */
    public static boolean validateCsvFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            return false;
        }
        
        return true;
    }

    /**
     * 获取CSV文件的列数
     * @param file CSV文件
     * @return 列数
     */
    public static int getColumnCount(MultipartFile file) {
        List<String> lines = readCsvFile(file);
        if (lines.isEmpty()) {
            return 0;
        }
        
        List<String> firstLine = parseCsvLine(lines.get(0));
        return firstLine.size();
    }

    /**
     * 获取CSV文件的表头
     * @param file CSV文件
     * @return 表头列表
     */
    public static List<String> getHeaders(MultipartFile file) {
        List<String> lines = readCsvFile(file);
        if (lines.isEmpty()) {
            return new ArrayList<>();
        }
        
        return parseCsvLine(lines.get(0));
    }

    /**
     * 获取CSV文件的数据行（不包含表头）
     * @param file CSV文件
     * @return 数据行列表
     */
    public static List<List<String>> getDataRows(MultipartFile file) {
        List<List<String>> allData = parseCsvFile(file);
        if (allData.size() <= 1) {
            return new ArrayList<>();
        }
        
        return allData.subList(1, allData.size());
    }
} 