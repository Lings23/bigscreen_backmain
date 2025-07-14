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
package me.zhengjie.modules.maint.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.maint.domain.Database;
import me.zhengjie.modules.maint.repository.DatabaseRepository;
import me.zhengjie.modules.maint.service.DatabaseService;
import me.zhengjie.modules.maint.service.dto.DatabaseDto;
import me.zhengjie.modules.maint.service.dto.DatabaseQueryCriteria;
import me.zhengjie.modules.maint.service.mapstruct.DatabaseMapper;
import me.zhengjie.modules.maint.util.CsvUtils;
import me.zhengjie.modules.maint.util.SqlUtils;
import me.zhengjie.utils.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
* @author zhanghouying
* @date 2019-08-24
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    private final DatabaseRepository databaseRepository;
    private final DatabaseMapper databaseMapper;

    @Override
    public PageResult<DatabaseDto> queryAll(DatabaseQueryCriteria criteria, Pageable pageable){
        Page<Database> page = databaseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(databaseMapper::toDto));
    }

    @Override
    public List<DatabaseDto> queryAll(DatabaseQueryCriteria criteria){
        return databaseMapper.toDto(databaseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public DatabaseDto findById(String id) {
        Database database = databaseRepository.findById(id).orElseGet(Database::new);
        ValidationUtil.isNull(database.getId(),"Database","id",id);
        return databaseMapper.toDto(database);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Database resources) {
        resources.setId(IdUtil.simpleUUID());
        databaseRepository.save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Database resources) {
        Database database = databaseRepository.findById(resources.getId()).orElseGet(Database::new);
        ValidationUtil.isNull(database.getId(),"Database","id",resources.getId());
        database.copy(resources);
        databaseRepository.save(database);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<String> ids) {
        for (String id : ids) {
            databaseRepository.deleteById(id);
        }
    }

    @Override
    public boolean testConnection(Database resources) {
        try {
            return SqlUtils.testConnection(resources.getJdbcUrl(), resources.getUserName(), resources.getPwd());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void download(List<DatabaseDto> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DatabaseDto databaseDto : queryAll) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("数据库名称", databaseDto.getName());
            map.put("数据库连接地址", databaseDto.getJdbcUrl());
            map.put("用户名", databaseDto.getUserName());
            map.put("创建日期", databaseDto.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Map<String, Object> readCsvFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证文件格式
            if (!CsvUtils.validateCsvFile(file)) {
                result.put("success", false);
                result.put("message", "文件格式无效，请上传CSV文件");
                return result;
            }
            
            // 获取表头
            List<String> headers = CsvUtils.getHeaders(file);
            // 获取数据行
            List<List<String>> dataRows = CsvUtils.getDataRows(file);
            
            result.put("success", true);
            result.put("headers", headers);
            result.put("data", dataRows);
            result.put("totalRows", dataRows.size());
            result.put("totalColumns", headers.size());
            
        } catch (Exception e) {
            log.error("读取CSV文件失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "读取CSV文件失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> importCsvToDatabase(MultipartFile file, String databaseId, String tableName) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证文件格式
            if (!CsvUtils.validateCsvFile(file)) {
                result.put("success", false);
                result.put("message", "文件格式无效，请上传CSV文件");
                return result;
            }
            
            // 获取数据库信息
            DatabaseDto database = findById(databaseId);
            if (database == null) {
                result.put("success", false);
                result.put("message", "数据库不存在");
                return result;
            }
            
            // 测试数据库连接
            if (!testConnection(databaseMapper.toEntity(database))) {
                result.put("success", false);
                result.put("message", "数据库连接失败");
                return result;
            }
            
            // 解析CSV数据
            List<String> headers = CsvUtils.getHeaders(file);
            List<List<String>> dataRows = CsvUtils.getDataRows(file);
            
            if (dataRows.isEmpty()) {
                result.put("success", false);
                result.put("message", "CSV文件没有数据行");
                return result;
            }
            
            // 生成建表SQL
            String createTableSql = generateCreateTableSql(tableName, headers);
            
            // 生成插入SQL
            List<String> insertSqls = generateInsertSqls(tableName, headers, dataRows);
            
            // 执行建表SQL
            String createResult = SqlUtils.executeSql(database.getJdbcUrl(), database.getUserName(), database.getPwd(), 
                    createTableSql);
            
            if (!"success".equals(createResult)) {
                result.put("success", false);
                result.put("message", "创建表失败: " + createResult);
                return result;
            }
            
            // 批量插入数据
            int successCount = 0;
            for (String insertSql : insertSqls) {
                String insertResult = SqlUtils.executeSql(database.getJdbcUrl(), database.getUserName(), database.getPwd(), 
                        insertSql);
                if ("success".equals(insertResult)) {
                    successCount++;
                }
            }
            
            result.put("success", true);
            result.put("message", String.format("导入成功，共导入 %d 条数据", successCount));
            result.put("totalRows", dataRows.size());
            result.put("successRows", successCount);
            
        } catch (Exception e) {
            log.error("导入CSV数据失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "导入失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 生成建表SQL
     * @param tableName 表名
     * @param headers 表头
     * @return 建表SQL
     */
    private String generateCreateTableSql(String tableName, List<String> headers) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
        sql.append("`id` INT AUTO_INCREMENT PRIMARY KEY,");
        
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            // 清理列名，移除特殊字符
            String columnName = header.replaceAll("[^a-zA-Z0-9_]", "_");
            sql.append("`").append(columnName).append("` VARCHAR(255)");
            if (i < headers.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        
        return sql.toString();
    }
    
    /**
     * 生成插入SQL列表
     * @param tableName 表名
     * @param headers 表头
     * @param dataRows 数据行
     * @return 插入SQL列表
     */
    private List<String> generateInsertSqls(String tableName, List<String> headers, List<List<String>> dataRows) {
        List<String> sqls = new ArrayList<>();
        
        for (List<String> row : dataRows) {
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO `").append(tableName).append("` (");
            
            // 添加列名
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i);
                String columnName = header.replaceAll("[^a-zA-Z0-9_]", "_");
                sql.append("`").append(columnName).append("`");
                if (i < headers.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") VALUES (");
            
            // 添加值
            for (int i = 0; i < row.size(); i++) {
                String value = row.get(i);
                // 处理特殊字符
                value = value.replace("'", "''");
                sql.append("'").append(value).append("'");
                if (i < row.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(")");
            
            sqls.add(sql.toString());
        }
        
        return sqls;
    }
}
