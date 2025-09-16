# 远程数据同步使用示例

## 快速开始

### 1. 添加远程数据库连接

首先在数据库管理页面添加远程数据库连接信息：

```json
{
  "name": "远程值班系统数据库",
  "jdbcUrl": "jdbc:mysql://192.168.1.100:3306/duty_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai",
  "userName": "remote_user",
  "pwd": "remote_password"
}
```

### 2. 测试连接

使用API测试远程数据库连接：

```bash
curl -X POST http://localhost:8000/api/remote-sync/test-connection/{databaseId}
```

### 3. 查看远程数据库结构

```bash
# 获取所有表名
curl http://localhost:8000/api/remote-sync/tables/{databaseId}

# 获取指定表的字段信息
curl http://localhost:8000/api/remote-sync/columns/{databaseId}/duty_schedule
```

### 4. 预览数据

```bash
# 预览远程表数据（限制100条）
curl "http://localhost:8000/api/remote-sync/preview/{databaseId}/duty_schedule?whereClause=duty_date%20>=%20'2024-01-01'"
```

### 5. 同步数据到本地

```bash
# 同步指定日期范围的值班数据
curl -X POST "http://localhost:8000/api/remote-sync/sync-duty/{databaseId}?startDate=2024-01-01&endDate=2024-01-31"
```

## 实际使用场景

### 场景1：定期同步值班数据

假设远程数据库有以下表结构：

**远程数据库表结构：**
```sql
-- 组织表
CREATE TABLE organization (
    org_id INT PRIMARY KEY,
    org_name VARCHAR(100)
);

-- 领导表
CREATE TABLE leader (
    leader_id INT PRIMARY KEY,
    org_id INT,
    leader_name VARCHAR(50),
    leader_phone VARCHAR(20)
);

-- 值班表
CREATE TABLE duty_schedule (
    duty_id INT PRIMARY KEY,
    org_id INT,
    duty_person VARCHAR(50),
    duty_phone VARCHAR(20),
    duty_date DATE
);
```

**本地数据库表结构：**
```sql
-- 本地值班表
CREATE TABLE duty_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_name VARCHAR(100),
    leader_name VARCHAR(50),
    leader_phone VARCHAR(20),
    duty_person VARCHAR(50),
    duty_phone VARCHAR(20),
    duty_date DATE,
    created_at DATETIME,
    updated_at DATETIME
);
```

**同步操作：**
1. 通过多表关联查询获取完整信息
2. 字段映射：远程字段 -> 本地字段
3. 数据去重：根据组织名称和值班日期
4. 批量插入本地数据库

### 场景2：多表数据聚合同步

如果需要从多个远程表聚合数据：

```sql
-- 远程查询SQL（在服务中已配置）
SELECT 
    o.org_name,
    l.leader_name,
    l.leader_phone,
    d.duty_person,
    d.duty_phone,
    d.duty_date
FROM organization o 
JOIN leader l ON o.org_id = l.org_id 
JOIN duty_schedule d ON o.org_id = d.org_id 
WHERE d.duty_date BETWEEN ? AND ?
ORDER BY d.duty_date, o.org_name
```

### 场景3：定时任务同步

可以配置定时任务定期同步数据：

```java
@Component
@RequiredArgsConstructor
public class DutyDataSyncTask {
    
    private final RemoteDataSyncService remoteDataSyncService;
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void syncDutyData() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();
        
        // 同步昨天的值班数据
        remoteDataSyncService.syncDutyDataToLocal("remote_db_id", yesterday, today);
    }
}
```

## 错误处理示例

### 连接失败
```json
{
  "success": false,
  "message": "连接失败"
}
```

**解决方案：**
- 检查网络连接
- 验证数据库连接信息
- 确认防火墙设置

### 数据同步失败
```json
{
  "success": false,
  "message": "同步失败: 字段映射不匹配"
}
```

**解决方案：**
- 检查字段映射配置
- 确认本地表结构
- 验证数据类型兼容性

### 部分数据同步成功
```json
{
  "success": true,
  "message": "同步完成：成功导入 25 条记录，跳过 3 条重复记录",
  "totalRead": 28,
  "successCount": 25,
  "skipCount": 3
}
```

## 性能优化建议

### 1. 批量处理
- 设置合理的批次大小（500-1000条）
- 使用事务保证数据一致性

### 2. 索引优化
- 为同步字段创建索引
- 优化查询条件

### 3. 连接池配置
- 合理配置连接池大小
- 设置连接超时时间

### 4. 异步处理
- 大数据量同步使用异步处理
- 提供同步进度查询接口

## 监控和日志

### 同步日志示例
```
2024-01-15 10:30:00 INFO  - 开始同步值班数据，数据库ID: remote_db_id
2024-01-15 10:30:05 INFO  - 从远程数据库读取到 28 条记录
2024-01-15 10:30:08 INFO  - 成功导入 25 条记录，跳过 3 条重复记录
2024-01-15 10:30:08 INFO  - 同步完成，耗时: 8秒
```

### 监控指标
- 同步成功率
- 同步耗时
- 数据量统计
- 错误率统计

这个远程数据同步功能完全基于现有的 `maint` 模块，无需额外配置，通过动态创建数据源的方式连接远程数据库，实现了灵活的多数据库数据同步能力。




