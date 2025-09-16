# CSV数据检验模块API文档

## 概述
CSV数据检验模块是一个由AI agent构建的智能数据验证系统，用于检验导入的CSV数据列是否符合数据库表结构规范。该模块通过AI阅读SQL文档，自动总结数据库字段的意义和约束，并结合前端选择的表结构信息，对CSV数据进行智能检验。

## 核心功能

- **动态检验**: 根据前端选择的表，动态筛选对应的数据类型和约束规则
- **智能提示**: 提供详细的数据格式错误提示和修复建议

## 数据库表结构
```sql
CREATE TABLE table_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    table_name VARCHAR(100) NOT NULL COMMENT '表名',
    column_name VARCHAR(100) NOT NULL COMMENT '字段名',
    column_type VARCHAR(50) NOT NULL COMMENT '字段类型',
    is_nullable BOOLEAN NOT NULL COMMENT '是否可空',
    default_value VARCHAR(255) DEFAULT NULL COMMENT '默认值',
    comment VARCHAR(500) DEFAULT NULL COMMENT '字段注释',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uniq_table_column (table_name, column_name)
) COMMENT='数据库表字段元数据表';
```

## API接口

### 1. 获取表结构信息
- **URL**: `GET /api/tableMetadata/columns/{tableName}`
- **权限**: `tableMetadata:list`
- **描述**: 根据前端选择的表名，获取该表的字段结构信息
- **路径参数**:
  - `tableName`: 前端选择的表名
- **响应示例**:
```json
[
    {
        "id": 1,
        "tableName": "asset_list",
        "columnName": "asset_ip",
        "columnType": "VARCHAR(45)",
        "isNullable": false,
        "defaultValue": null,
        "comment": "资产IP地址"
    },
    {
        "id": 2,
        "tableName": "asset_list",
        "columnName": "asset_port",
        "columnType": "INT UNSIGNED",
        "isNullable": false,
        "defaultValue": "0",
        "comment": "资产端口，默认0"
    }
]
```

### 2. 获取所有表名列表
- **URL**: `GET /api/tableMetadata/tables`
- **权限**: `tableMetadata:list`
- **描述**: 获取系统中所有可用的表名，供前端选择框使用
- **响应示例**:
```json
[
    "asset_list",
    "user",
    "role",
    "menu"
]
```

### 3. CSV数据检验接口
- **URL**: `POST /api/csv/validate`
- **权限**: `csv:validate`
- **描述**: 检验CSV数据是否符合选定表的结构规范
- **请求体**:
```json
{
    "tableName": "asset_list",
    "csvHeaders": ["asset_ip", "asset_port", "system_name"],
    "csvSampleData": [
        ["192.168.1.100", "80", "Web服务器"],
        ["192.168.1.101", "443", "HTTPS服务器"]
    ]
}
```
- **响应示例**:
```json
{
    "valid": false,
    "summary": {
        "totalColumns": 3,
        "validColumns": 2,
        "invalidColumns": 1,
        "validationTime": "2025-01-15 10:30:00"
    },
    "errors": [
        {
            "columnName": "asset_port",
            "errorType": "TYPE_MISMATCH",
            "message": "字段类型不匹配，期望数字类型，实际为字符串",
            "expectedType": "INT UNSIGNED",
            "actualValue": "80",
            "suggestion": "请确保端口号为数字格式"
        }
    ],
    "warnings": [
        {
            "columnName": "system_name",
            "warningType": "MISSING_COLUMN",
            "message": "CSV中缺少必填字段",
            "suggestion": "请在CSV中添加system_name列"
        }
    ]
}
```

## AI Agent检验逻辑

### 1. AI文档解析流程
```javascript
// AI Agent解析SQL文档
class SqlDocumentAnalyzer {
    constructor(sqlContent) {
        this.sqlContent = sqlContent;
        this.analyzedTables = this.analyzeTables();
    }
    
    // AI分析表结构
    analyzeTables() {
        const tables = [];
        const createTableRegex = /CREATE\s+TABLE\s+`?(\w+)`?\s*\(([\s\S]*?)\)\s*;?/gi;
        let match;
        
        while ((match = createTableRegex.exec(this.sqlContent)) !== null) {
            const tableName = match[1];
            const tableBody = match[2];
            const analysis = this.analyzeTableStructure(tableName, tableBody);
            
            tables.push(analysis);
        }
        
        return tables;
    }
    
    // AI分析单个表结构
    analyzeTableStructure(tableName, tableBody) {
        const columns = this.parseColumns(tableBody);
        const constraints = this.extractConstraints(tableBody);
        const businessRules = this.inferBusinessRules(tableName, columns);
        
        return {
            tableName: tableName,
            columns: columns,
            constraints: constraints,
            businessRules: businessRules,
            aiSummary: this.generateTableSummary(tableName, columns)
        };
    }
    
    // AI推断业务规则
    inferBusinessRules(tableName, columns) {
        const rules = [];
        
        columns.forEach(column => {
            // 基于字段名和注释推断业务规则
            if (column.name.includes('ip') || column.comment.includes('IP')) {
                rules.push({
                    columnName: column.name,
                    ruleType: 'IP_FORMAT',
                    pattern: /^(\d{1,3}\.){3}\d{1,3}$/,
                    message: 'IP地址格式不正确'
                });
            }
            
            if (column.name.includes('port') || column.comment.includes('端口')) {
                rules.push({
                    columnName: column.name,
                    ruleType: 'PORT_RANGE',
                    minValue: 1,
                    maxValue: 65535,
                    message: '端口号应在1-65535范围内'
                });
            }
            
            if (column.name.includes('email') || column.comment.includes('邮箱')) {
                rules.push({
                    columnName: column.name,
                    ruleType: 'EMAIL_FORMAT',
                    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: '邮箱格式不正确'
                });
            }
        });
        
        return rules;
    }
    
    // AI生成表结构总结
    generateTableSummary(tableName, columns) {
        const summary = {
            tablePurpose: this.inferTablePurpose(tableName),
            keyFields: this.identifyKeyFields(columns),
            dataValidationRules: this.generateValidationRules(columns),
            importGuidelines: this.generateImportGuidelines(columns)
        };
        
        return summary;
    }
}
```

### 2. CSV数据检验器
```javascript
// CSV数据检验器
class CsvDataValidator {
    constructor(tableMetadata, businessRules) {
        this.tableMetadata = tableMetadata;
        this.businessRules = businessRules;
        this.validationResults = [];
    }
    
    // 执行CSV数据检验
    validateCsvData(csvHeaders, csvSampleData) {
        this.validationResults = [];
        
        // 1. 检验CSV列名与表字段的匹配
        this.validateColumnMapping(csvHeaders);
        
        // 2. 检验数据类型
        this.validateDataTypes(csvHeaders, csvSampleData);
        
        // 3. 检验业务规则
        this.validateBusinessRules(csvHeaders, csvSampleData);
        
        // 4. 检验约束条件
        this.validateConstraints(csvHeaders, csvSampleData);
        
        return this.generateValidationReport();
    }
    
    // 检验列名映射
    validateColumnMapping(csvHeaders) {
        const tableColumns = this.tableMetadata.map(col => col.columnName);
        
        csvHeaders.forEach((header, index) => {
            if (!tableColumns.includes(header)) {
                this.addError('COLUMN_NOT_FOUND', header, 
                    `CSV列名 '${header}' 在表结构中不存在`,
                    null, header);
            }
        });
        
        // 检查必填字段是否缺失
        this.tableMetadata.forEach(column => {
            if (!column.isNullable && !csvHeaders.includes(column.columnName)) {
                this.addWarning('MISSING_REQUIRED_COLUMN', column.columnName,
                    `缺少必填字段 '${column.columnName}'`,
                    `请在CSV中添加 ${column.columnName} 列`);
            }
        });
    }
    
    // 检验数据类型
    validateDataTypes(csvHeaders, csvSampleData) {
        csvHeaders.forEach((header, colIndex) => {
            const tableColumn = this.tableMetadata.find(col => col.columnName === header);
            if (!tableColumn) return;
            
            csvSampleData.forEach((row, rowIndex) => {
                const value = row[colIndex];
                if (value === null || value === undefined || value === '') {
                    if (!tableColumn.isNullable) {
                        this.addError('NULL_VALUE_IN_NOT_NULL_COLUMN', header,
                            `第${rowIndex + 1}行，字段 '${header}' 不能为空`,
                            tableColumn.columnType, value);
                    }
                    return;
                }
                
                // 根据数据库类型检验数据格式
                if (!this.isValidDataType(value, tableColumn.columnType)) {
                    this.addError('TYPE_MISMATCH', header,
                        `第${rowIndex + 1}行，字段 '${header}' 类型不匹配，期望 ${tableColumn.columnType}`,
                        tableColumn.columnType, value);
                }
            });
        });
    }
    
    // 检验业务规则
    validateBusinessRules(csvHeaders, csvSampleData) {
        this.businessRules.forEach(rule => {
            const colIndex = csvHeaders.indexOf(rule.columnName);
            if (colIndex === -1) return;
            
            csvSampleData.forEach((row, rowIndex) => {
                const value = row[colIndex];
                if (!this.validateBusinessRule(value, rule)) {
                    this.addError('BUSINESS_RULE_VIOLATION', rule.columnName,
                        `第${rowIndex + 1}行，${rule.message}`,
                        rule.ruleType, value);
                }
            });
        });
    }
    
    // 检验约束条件
    validateConstraints(csvHeaders, csvSampleData) {
        // 检验唯一性约束
        this.validateUniqueConstraints(csvHeaders, csvSampleData);
        
        // 检验长度限制
        this.validateLengthConstraints(csvHeaders, csvSampleData);
        
        // 检验默认值
        this.validateDefaultValues(csvHeaders, csvSampleData);
    }
    
    // 数据类型验证
    isValidDataType(value, columnType) {
        const type = columnType.toUpperCase();
        
        if (type.includes('VARCHAR') || type.includes('CHAR')) {
            const maxLength = this.extractLength(columnType);
            return typeof value === 'string' && value.length <= maxLength;
        }
        
        if (type.includes('INT') || type.includes('BIGINT')) {
            return !isNaN(value) && Number.isInteger(Number(value));
        }
        
        if (type.includes('DECIMAL') || type.includes('FLOAT') || type.includes('DOUBLE')) {
            return !isNaN(value);
        }
        
        if (type.includes('DATETIME') || type.includes('TIMESTAMP')) {
            return this.isValidDateTime(value);
        }
        
        if (type.includes('BOOLEAN') || type.includes('BOOL')) {
            return ['true', 'false', '1', '0', 'yes', 'no'].includes(value.toLowerCase());
        }
        
        return true; // 默认通过
    }
    
    // 业务规则验证
    validateBusinessRule(value, rule) {
        switch (rule.ruleType) {
            case 'IP_FORMAT':
                return rule.pattern.test(value);
            case 'PORT_RANGE':
                const port = parseInt(value);
                return port >= rule.minValue && port <= rule.maxValue;
            case 'EMAIL_FORMAT':
                return rule.pattern.test(value);
            default:
                return true;
        }
    }
    
    // 生成检验报告
    generateValidationReport() {
        const totalErrors = this.validationResults.filter(r => r.type === 'error').length;
        const totalWarnings = this.validationResults.filter(r => r.type === 'warning').length;
        
        return {
            valid: totalErrors === 0,
            summary: {
                totalColumns: this.tableMetadata.length,
                validColumns: this.tableMetadata.length - totalErrors,
                invalidColumns: totalErrors,
                warnings: totalWarnings,
                validationTime: new Date().toLocaleString()
            },
            errors: this.validationResults.filter(r => r.type === 'error'),
            warnings: this.validationResults.filter(r => r.type === 'warning'),
            suggestions: this.generateSuggestions()
        };
    }
}
```

### 3. 前端集成示例
```javascript
// 前端CSV检验集成
class CsvValidationFrontend {
    constructor() {
        this.selectedTable = null;
        this.csvData = null;
        this.validator = null;
    }
    
    // 初始化检验器
    async initializeValidator(tableName) {
        try {
            // 1. 获取表结构信息
            const tableResponse = await fetch(`/api/tableMetadata/columns/${tableName}`);
            const tableMetadata = await tableResponse.json();
            
            // 2. 获取AI分析的业务规则
            const rulesResponse = await fetch(`/api/ai/business-rules/${tableName}`);
            const businessRules = await rulesResponse.json();
            
            // 3. 初始化检验器
            this.validator = new CsvDataValidator(tableMetadata, businessRules);
            this.selectedTable = tableName;
            
            console.log('检验器初始化成功');
        } catch (error) {
            console.error('检验器初始化失败:', error);
        }
    }
    
    // 检验CSV数据
    async validateCsvFile(file) {
        if (!this.validator) {
            alert('请先选择要导入的表');
            return;
        }
        
        try {
            // 1. 解析CSV文件
            const csvData = await this.parseCsvFile(file);
            
            // 2. 执行检验
            const validationResult = this.validator.validateCsvData(
                csvData.headers, 
                csvData.sampleData
            );
            
            // 3. 显示检验结果
            this.displayValidationResult(validationResult);
            
            // 4. 如果检验通过，允许导入
            if (validationResult.valid) {
                this.enableImportButton();
            } else {
                this.disableImportButton();
            }
            
        } catch (error) {
            console.error('CSV检验失败:', error);
            alert('CSV文件解析失败: ' + error.message);
        }
    }
    
    // 显示检验结果
    displayValidationResult(result) {
        const resultContainer = document.getElementById('validation-result');
        
        let html = '<div class="validation-summary">';
        html += `<h4>检验结果: ${result.valid ? '通过' : '失败'}</h4>`;
        html += `<p>总字段数: ${result.summary.totalColumns}</p>`;
        html += `<p>有效字段: ${result.summary.validColumns}</p>`;
        html += `<p>错误字段: ${result.summary.invalidColumns}</p>`;
        html += `<p>警告数量: ${result.summary.warnings}</p>`;
        html += '</div>';
        
        if (result.errors.length > 0) {
            html += '<div class="validation-errors">';
            html += '<h5>错误详情:</h5><ul>';
            result.errors.forEach(error => {
                html += `<li><strong>${error.columnName}:</strong> ${error.message}`;
                if (error.suggestion) {
                    html += `<br><small>建议: ${error.suggestion}</small>`;
                }
                html += '</li>';
            });
            html += '</ul></div>';
        }
        
        if (result.warnings.length > 0) {
            html += '<div class="validation-warnings">';
            html += '<h5>警告信息:</h5><ul>';
            result.warnings.forEach(warning => {
                html += `<li><strong>${warning.columnName}:</strong> ${warning.message}`;
                if (warning.suggestion) {
                    html += `<br><small>建议: ${warning.suggestion}</small>`;
                }
                html += '</li>';
            });
            html += '</ul></div>';
        }
        
        resultContainer.innerHTML = html;
    }
}
```

## 使用流程

### 1. 前端操作流程
1. **选择目标表**: 用户从下拉框中选择要导入数据的表
2. **上传CSV文件**: 用户选择并上传CSV文件
3. **自动检验**: 系统自动检验CSV数据格式
4. **查看结果**: 显示检验结果和错误提示
5. **修正数据**: 根据提示修正CSV数据
6. **确认导入**: 检验通过后确认导入

### 2. AI Agent工作流程
1. **解析SQL文档**: AI读取并分析SQL建表语句
2. **理解表结构**: 自动识别字段类型、约束和业务规则
3. **生成检验规则**: 基于表结构生成数据检验规则
4. **动态检验**: 根据前端选择的表执行检验
5. **智能提示**: 提供详细的错误分析和修复建议

## 检验规则类型

### 1. 基础数据类型检验
- **字符串类型**: VARCHAR, CHAR, TEXT
- **数字类型**: INT, BIGINT, DECIMAL, FLOAT
- **日期时间**: DATETIME, TIMESTAMP, DATE
- **布尔类型**: BOOLEAN, BOOL

### 2. 业务规则检验
- **IP地址格式**: 验证IP地址格式正确性
- **端口号范围**: 验证端口号在1-65535范围内
- **邮箱格式**: 验证邮箱地址格式
- **手机号格式**: 验证手机号格式
- **身份证号**: 验证身份证号格式

### 3. 约束条件检验
- **非空约束**: 检查必填字段是否有值
- **长度限制**: 检查字符串长度是否超限
- **唯一性约束**: 检查重复数据
- **默认值**: 检查默认值格式

## 错误类型和提示

### 1. 列名错误
- **错误**: CSV列名在表结构中不存在
- **提示**: "列名 'xxx' 在表 'yyy' 中不存在，请检查列名拼写"

### 2. 数据类型错误
- **错误**: 数据格式与字段类型不匹配
- **提示**: "字段 'xxx' 期望数字类型，实际为字符串，请修正数据格式"

### 3. 业务规则错误
- **错误**: 数据不符合业务规则
- **提示**: "IP地址格式不正确，请使用标准IP地址格式 (如: 192.168.1.1)"

### 4. 约束违反
- **错误**: 违反数据库约束条件
- **提示**: "字段 'xxx' 不能为空，请填写必填数据"

## 优势特点

### 1. AI智能分析
- **自动理解**: AI自动分析SQL文档，理解表结构含义
- **智能推断**: 基于字段名和注释推断业务规则
- **动态适应**: 支持新增表和字段的自动识别

### 2. 用户体验优化
- **实时检验**: 文件上传后立即进行检验
- **详细提示**: 提供具体的错误位置和修复建议
- **可视化展示**: 清晰的检验结果展示界面

### 3. 系统集成
- **无缝集成**: 与现有CSV导入功能无缝集成
- **权限控制**: 支持细粒度的权限控制
- **扩展性强**: 支持自定义检验规则扩展

## 注意事项
1. CSV文件编码建议使用UTF-8，避免中文乱码
2. 大文件检验可能需要较长时间，建议添加进度提示
3. 检验规则会定期更新，确保与数据库结构同步
4. 建议在导入前进行数据备份，防止数据丢失
