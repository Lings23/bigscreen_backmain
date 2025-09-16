# 远程数据同步API文档

## 概述

本模块提供了从远程数据库读取数据并同步到本地数据库的功能。通过 `maint` 模块的 `Database` 实体管理远程数据库连接信息，然后使用同步服务将远程数据读取并转换成本地实体格式存储。

## 功能特性

- ✅ 支持多远程数据库连接管理
- ✅ 自动字段映射和数据转换
- ✅ 支持自定义SQL查询
- ✅ 支持批量同步多个表
- ✅ 数据去重和冲突处理
- ✅ 同步进度监控
- ✅ 事务安全保证

## 使用流程

### 1. 配置远程数据库连接

首先在数据库管理模块中添加远程数据库连接信息：

```http
POST /api/database
Content-Type: application/json

{
  "name": "远程值班系统数据库",
  "jdbcUrl": "jdbc:mysql://192.168.1.100:3306/duty_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai",
  "userName": "remote_user",
  "pwd": "remote_password"
}
```

### 2. 测试远程数据库连接

```http
POST /api/remote-sync/test-connection/{databaseId}
```

### 3. 查看远程数据库表结构

```http
GET /api/remote-sync/tables/{databaseId}
```

```http
GET /api/remote-sync/columns/{databaseId}/{tableName}
```

### 4. 预览远程数据

```http
GET /api/remote-sync/preview/{databaseId}/{tableName}?whereClause=duty_date >= '2024-01-01'
```

### 5. 同步数据到本地

#### 方式一：同步值班数据（专用接口）

```http
POST /api/remote-sync/sync-duty/{databaseId}?startDate=2024-01-01&endDate=2024-01-31
```

#### 方式二：通用表数据同步

```http
POST /api/remote-sync/generic-sync
Content-Type: application/json

{
  "databaseId": "remote_db_id",
  "remoteTableName": "duty_schedule",
  "localTableName": "duty_schedule",
  "fieldMapping": {
    "org_name": "orgName",
    "leader_name": "leaderName",
    "leader_phone": "leaderPhone",
    "duty_person": "dutyPerson",
    "duty_phone": "dutyPhone",
    "duty_date": "dutyDate"
  },
  "whereClause": "duty_date BETWEEN '2024-01-01' AND '2024-01-31'",
  "limit": 1000
}
```

## API接口详情

### 1. 测试远程数据库连接

**接口地址：** `POST /api/remote-sync/test-connection/{databaseId}`

**功能：** 测试指定远程数据库的连接是否正常

**响应示例：**
```json
{
  "success": true,
  "message": "连接成功"
}
```

### 2. 获取远程数据库表列表

**接口地址：** `GET /api/remote-sync/tables/{databaseId}`

**功能：** 获取远程数据库中的所有表名

**响应示例：**
```json
[
  "organization",
  "leader",
  "duty_schedule",
  "user_info"
]
```

### 3. 获取远程表字段信息

**接口地址：** `GET /api/remote-sync/columns/{databaseId}/{tableName}`

**功能：** 获取指定表的字段详细信息

**响应示例：**
```json
[
  {
    "columnName": "org_name",
    "dataType": "VARCHAR",
    "columnSize": 100,
    "nullable": 1,
    "columnDefault": null,
    "remarks": "组织名称"
  },
  {
    "columnName": "duty_date",
    "dataType": "DATE",
    "columnSize": 10,
    "nullable": 0,
    "columnDefault": null,
    "remarks": "值班日期"
  }
]
```

### 4. 读取远程表数据

**接口地址：** `GET /api/remote-sync/data/{databaseId}/{tableName}`

**参数：**
- `whereClause` (可选): WHERE条件
- `limit` (可选): 限制返回条数

**响应示例：**
```json
[
  {
    "org_name": "技术部",
    "leader_name": "张三",
    "leader_phone": "13800138000",
    "duty_person": "李四",
    "duty_phone": "13900139000",
    "duty_date": "2024-01-15"
  }
]
```

### 5. 同步值班数据到本地

**接口地址：** `POST /api/remote-sync/sync-duty/{databaseId}`

**参数：**
- `startDate`: 开始日期 (YYYY-MM-DD)
- `endDate`: 结束日期 (YYYY-MM-DD)

**响应示例：**
```json
{
  "success": true,
  "message": "同步完成：成功导入 25 条记录，跳过 3 条重复记录",
  "totalRead": 28,
  "successCount": 25,
  "skipCount": 3
}
```

### 6. 预览远程数据

**接口地址：** `GET /api/remote-sync/preview/{databaseId}/{tableName}`

**参数：**
- `whereClause` (可选): WHERE条件

**响应示例：**
```json
{
  "success": true,
  "data": [...],
  "count": 100,
  "tableName": "duty_schedule"
}
```

## 数据同步配置示例

### 值班数据同步配置

```json
{
  "databaseId": "remote_duty_db",
  "remoteTableName": "duty_schedule",
  "localTableName": "duty_schedule",
  "fieldMapping": {
    "org_name": "orgName",
    "leader_name": "leaderName", 
    "leader_phone": "leaderPhone",
    "duty_person": "dutyPerson",
    "duty_phone": "dutyPhone",
    "duty_date": "dutyDate"
  },
  "whereClause": "duty_date >= CURDATE() - INTERVAL 30 DAY",
  "limit": 1000
}
```

### 多表关联查询同步

```json
{
  "databaseId": "remote_duty_db",
  "customSql": "SELECT o.org_name, l.leader_name, l.leader_phone, d.duty_person, d.duty_phone, d.duty_date FROM organization o JOIN leader l ON o.org_id = l.org_id JOIN duty_schedule d ON o.org_id = d.org_id WHERE d.duty_date BETWEEN ? AND ?",
  "localTableName": "duty_schedule",
  "fieldMapping": {
    "org_name": "orgName",
    "leader_name": "leaderName",
    "leader_phone": "leaderPhone", 
    "duty_person": "dutyPerson",
    "duty_phone": "dutyPhone",
    "duty_date": "dutyDate"
  },
  "parameters": ["2024-01-01", "2024-01-31"]
}
```

## 错误处理

### 常见错误及解决方案

1. **连接失败**
   ```
   错误：连接失败
   解决：检查数据库连接信息、网络连接、防火墙设置
   ```

2. **表不存在**
   ```
   错误：表不存在
   解决：检查表名是否正确，确认远程数据库权限
   ```

3. **字段映射错误**
   ```
   错误：字段映射不匹配
   解决：检查字段映射配置，确认本地表结构
   ```

4. **数据格式错误**
   ```
   错误：数据格式转换失败
   解决：检查数据类型兼容性，调整字段映射
   ```

## 安全注意事项

1. **数据库连接安全**
   - 使用加密连接（SSL/TLS）
   - 限制数据库用户权限
   - 定期更换密码

2. **数据传输安全**
   - 使用HTTPS传输
   - 敏感数据加密存储
   - 访问权限控制

3. **操作审计**
   - 记录所有同步操作
   - 监控异常访问
   - 定期安全审计

## 性能优化建议

1. **批量处理**
   - 使用批量插入减少数据库交互
   - 合理设置批次大小（建议500-1000条）

2. **索引优化**
   - 为同步字段创建索引
   - 优化查询条件

3. **连接池配置**
   - 合理配置连接池大小
   - 设置连接超时时间

4. **异步处理**
   - 大数据量同步使用异步处理
   - 提供同步进度查询接口

## 使用示例

### 完整的数据同步流程

```bash
# 1. 添加远程数据库连接
curl -X POST http://localhost:8000/api/database \
  -H "Content-Type: application/json" \
  -d '{
    "name": "远程值班系统",
    "jdbcUrl": "jdbc:mysql://192.168.1.100:3306/duty_system",
    "userName": "remote_user",
    "pwd": "remote_password"
  }'

# 2. 测试连接
curl -X POST http://localhost:8000/api/remote-sync/test-connection/remote_db_id

# 3. 查看表结构
curl http://localhost:8000/api/remote-sync/tables/remote_db_id

# 4. 预览数据
curl "http://localhost:8000/api/remote-sync/preview/remote_db_id/duty_schedule?whereClause=duty_date%20>=%20'2024-01-01'"

# 5. 同步数据
curl -X POST "http://localhost:8000/api/remote-sync/sync-duty/remote_db_id?startDate=2024-01-01&endDate=2024-01-31"
```

这个远程数据同步功能完全基于现有的 `maint` 模块，无需额外配置数据源，通过动态创建数据源的方式连接远程数据库，实现了灵活的多数据库数据同步能力。




