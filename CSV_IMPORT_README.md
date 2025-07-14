# CSV文件导入功能说明

## 功能概述

本项目新增了CSV文件读取和导入数据库的功能，主要包含以下特性：

1. **CSV文件读取** - 解析CSV文件内容，提取表头和数据
2. **CSV数据导入** - 将CSV数据导入到指定的数据库中
3. **自动建表** - 根据CSV表头自动创建数据库表
4. **数据验证** - 验证CSV文件格式和数据库连接

## 数据库交互代码位置

### 1. Repository层（数据访问层）
- **位置**: `eladmin-system/src/main/java/me/zhengjie/modules/*/repository/`
- **主要文件**:
  - `DatabaseRepository.java` - 数据库管理
  - `UserRepository.java` - 用户管理
  - `DictRepository.java` - 字典管理

### 2. Service层（业务逻辑层）
- **位置**: `eladmin-system/src/main/java/me/zhengjie/modules/*/service/`
- **主要文件**:
  - `DatabaseServiceImpl.java` - 数据库服务实现（包含CSV处理逻辑）
  - `UserServiceImpl.java` - 用户服务实现

### 3. Controller层（控制层）
- **位置**: `eladmin-system/src/main/java/me/zhengjie/modules/*/rest/`
- **主要文件**:
  - `DatabaseController.java` - 数据库管理接口（包含CSV处理接口）

## 新增的CSV处理功能

### 1. CsvUtils工具类
**位置**: `eladmin-system/src/main/java/me/zhengjie/modules/maint/util/CsvUtils.java`

**主要方法**:
- `readCsvFile(MultipartFile file)` - 读取CSV文件内容
- `parseCsvLine(String line)` - 解析CSV行数据
- `parseCsvFile(MultipartFile file)` - 解析CSV文件为二维数组
- `validateCsvFile(MultipartFile file)` - 验证CSV文件格式
- `getHeaders(MultipartFile file)` - 获取CSV文件表头
- `getDataRows(MultipartFile file)` - 获取CSV文件数据行

### 2. 数据库服务扩展
**位置**: `eladmin-system/src/main/java/me/zhengjie/modules/maint/service/DatabaseService.java`

**新增方法**:
- `readCsvFile(MultipartFile file)` - 读取CSV文件并解析内容
- `importCsvToDatabase(MultipartFile file, String databaseId, String tableName)` - 将CSV数据导入到指定数据库

### 3. API接口
**位置**: `eladmin-system/src/main/java/me/zhengjie/modules/maint/rest/DatabaseController.java`

**新增接口**:
- `POST /api/database/readCsv` - 读取CSV文件
- `POST /api/database/importCsv` - 导入CSV数据到数据库

## 使用方法

### 1. 读取CSV文件

**请求**:
```http
POST /api/database/readCsv
Content-Type: multipart/form-data

file: [CSV文件]
```

**响应**:
```json
{
  "success": true,
  "headers": ["姓名", "年龄", "城市", "职业"],
  "data": [
    ["张三", "25", "北京", "工程师"],
    ["李四", "30", "上海", "设计师"]
  ],
  "totalRows": 2,
  "totalColumns": 4
}
```

### 2. 导入CSV数据到数据库

**请求**:
```http
POST /api/database/importCsv
Content-Type: multipart/form-data

file: [CSV文件]
databaseId: [数据库ID]
tableName: [目标表名]
```

**响应**:
```json
{
  "success": true,
  "message": "导入成功，共导入 4 条数据",
  "totalRows": 4,
  "successRows": 4
}
```

## CSV文件格式要求

1. **文件格式**: 必须是.csv文件
2. **编码**: UTF-8编码
3. **分隔符**: 逗号(,)
4. **引号处理**: 支持双引号转义
5. **表头**: 第一行作为表头
6. **数据行**: 从第二行开始为数据

## 示例CSV文件

```csv
姓名,年龄,城市,职业
张三,25,北京,工程师
李四,30,上海,设计师
王五,28,广州,产品经理
赵六,35,深圳,销售经理
```

## 注意事项

1. **数据库连接**: 导入前会验证数据库连接
2. **表名规范**: 表名会自动处理特殊字符
3. **列名规范**: 列名会自动清理特殊字符
4. **数据类型**: 所有字段默认创建为VARCHAR(255)
5. **错误处理**: 详细的错误信息会返回给客户端

## 权限要求

- 读取CSV文件: `database:add` 权限
- 导入CSV数据: `database:add` 权限

## 技术实现

1. **文件处理**: 使用Java IO流读取CSV文件
2. **数据解析**: 自定义CSV解析器，支持引号转义
3. **SQL生成**: 动态生成建表和插入SQL
4. **数据库操作**: 使用Druid连接池执行SQL
5. **错误处理**: 完善的异常处理和日志记录 