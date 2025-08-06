package me.zhengjie.modules.csv.controller;

import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.csv.dto.CsvImportRequest;
import me.zhengjie.modules.csv.dto.CsvPreviewRequest;
import me.zhengjie.modules.csv.service.CsvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvService;

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvService.uploadCsv(file));
    }

    @PostMapping("/preview")
    public ResponseEntity<Object> previewCsv(@RequestBody CsvPreviewRequest request) {
        return ResponseEntity.ok(csvService.previewCsv(request));
    }

    @PostMapping("/import")
    public ResponseEntity<Object> importCsv(@RequestBody CsvImportRequest request) {
        return ResponseEntity.ok(csvService.importCsv(request));
    }

    @GetMapping("/history")
    public ResponseEntity<Object> getHistory(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(csvService.getHistory(page, size));
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        csvService.downloadTemplate(response);
    }

    @GetMapping("/tables")
    public ResponseEntity<Object> getAllTables() {
        return ResponseEntity.ok(csvService.getAllTables());
    }

    @PostMapping("/createTable")
    public ResponseEntity<Object> createTable(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(csvService.createTable(request));
    }
} 