package me.zhengjie.modules.csv;

import me.zhengjie.modules.csv.util.CsvUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CsvImportTest {

    @Test
    public void testCsvHeadersFiltering(@TempDir Path tempDir) throws IOException {
        // 创建测试CSV文件
        File csvFile = tempDir.resolve("test.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("id,name,age,created_at,updated_at,email\n");
            writer.write("1,张三,25,2023-01-01,2023-01-01,zhangsan@example.com\n");
            writer.write("2,李四,30,2023-01-02,2023-01-02,lisi@example.com\n");
        }

        // 测试getHeaders方法是否正确过滤字段
        List<String> headers = CsvUtils.getHeaders(csvFile);
        
        // 验证过滤后的字段
        assertTrue(headers.contains("name"));
        assertTrue(headers.contains("age"));
        assertTrue(headers.contains("email"));
        assertFalse(headers.contains("id"));
        assertFalse(headers.contains("created_at"));
        assertFalse(headers.contains("updated_at"));
        
        assertEquals(3, headers.size());
    }

    @Test
    public void testCsvPreviewFiltering(@TempDir Path tempDir) throws IOException {
        // 创建测试CSV文件
        File csvFile = tempDir.resolve("test.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("id,name,age,created_at,updated_at,email\n");
            writer.write("1,张三,25,2023-01-01,2023-01-01,zhangsan@example.com\n");
            writer.write("2,李四,30,2023-01-02,2023-01-02,lisi@example.com\n");
        }

        // 测试previewRows方法是否正确过滤字段
        List<Map<String, String>> previewData = CsvUtils.previewRows(csvFile, 2);
        
        assertEquals(2, previewData.size());
        
        // 验证第一行数据
        Map<String, String> firstRow = previewData.get(0);
        assertEquals("张三", firstRow.get("name"));
        assertEquals("25", firstRow.get("age"));
        assertEquals("zhangsan@example.com", firstRow.get("email"));
        assertNull(firstRow.get("id"));
        assertNull(firstRow.get("created_at"));
        assertNull(firstRow.get("updated_at"));
        
        // 验证第二行数据
        Map<String, String> secondRow = previewData.get(1);
        assertEquals("李四", secondRow.get("name"));
        assertEquals("30", secondRow.get("age"));
        assertEquals("lisi@example.com", secondRow.get("email"));
    }

    @Test
    public void testCaseInsensitiveFiltering(@TempDir Path tempDir) throws IOException {
        // 创建测试CSV文件，使用大写字段名
        File csvFile = tempDir.resolve("test.csv").toFile();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.write("ID,NAME,AGE,CREATED_AT,UPDATED_AT,EMAIL\n");
            writer.write("1,张三,25,2023-01-01,2023-01-01,zhangsan@example.com\n");
        }

        // 测试大小写不敏感的过滤
        List<String> headers = CsvUtils.getHeaders(csvFile);
        
        assertTrue(headers.contains("NAME"));
        assertTrue(headers.contains("AGE"));
        assertTrue(headers.contains("EMAIL"));
        assertFalse(headers.contains("ID"));
        assertFalse(headers.contains("CREATED_AT"));
        assertFalse(headers.contains("UPDATED_AT"));
        
        assertEquals(3, headers.size());
    }
} 