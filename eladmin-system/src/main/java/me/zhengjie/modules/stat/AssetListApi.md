# 资产列表API文档

## 概述
资产列表模块提供了对IT资产的管理功能，包括资产的增删改查、分页查询、条件搜索等功能。

## 数据库表结构
```sql
CREATE TABLE asset_list (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    asset_ip VARCHAR(45) NOT NULL COMMENT '资产IP地址',
    asset_port INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '资产端口，默认0',
    system_name VARCHAR(100) DEFAULT NULL COMMENT '资产系统名称',
    organization_name VARCHAR(100) DEFAULT NULL COMMENT '所属单位',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '资产创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '资产更新时间',
    UNIQUE KEY uniq_ip_port (asset_ip, asset_port)
) COMMENT='资产列表表';
```

## API接口

### 1. 创建资产
- **URL**: `POST /api/asset-list`
- **权限**: `asset:add`
- **请求体**:
```json
{
    "assetIp": "192.168.1.100",
    "assetPort": 80,
    "systemName": "Web服务器",
    "organizationName": "技术部"
}
```
- **响应**: 返回创建的资产信息

### 2. 更新资产
- **URL**: `PUT /api/asset-list/{id}`
- **权限**: `asset:edit`
- **请求体**: 同创建资产
- **响应**: 返回更新后的资产信息

### 3. 删除资产
- **URL**: `DELETE /api/asset-list/{id}`
- **权限**: `asset:del`
- **响应**: 204 No Content

### 4. 批量删除资产
- **URL**: `DELETE /api/asset-list`
- **权限**: `asset:del`
- **请求体**: `[1, 2, 3]` (资产ID列表)
- **响应**: 204 No Content

### 5. 根据ID获取资产
- **URL**: `GET /api/asset-list/{id}`
- **权限**: `asset:list`
- **响应**: 返回资产信息

### 6. 根据IP和端口获取资产
- **URL**: `GET /api/asset-list/ip-port?assetIp=192.168.1.100&assetPort=80`
- **权限**: `asset:list`
- **响应**: 返回资产信息

### 7. 分页查询资产列表
- **URL**: `GET /api/asset-list`
- **权限**: `asset:list`
- **查询参数**:
  - `assetIp`: IP地址（模糊查询）
  - `systemName`: 系统名称（模糊查询）
  - `organizationName`: 所属单位（模糊查询）
  - `page`: 页码（默认1）
  - `size`: 每页大小（默认10）
  - `sortBy`: 排序字段（默认createdAt）
  - `sortDirection`: 排序方向（默认desc）
- **响应**: 返回分页结果

### 8. 获取所有资产列表
- **URL**: `GET /api/asset-list/all`
- **权限**: `asset:list`
- **响应**: 返回所有资产列表

### 9. 根据条件查询资产列表
- **URL**: `GET /api/asset-list/search`
- **权限**: `asset:list`
- **查询参数**:
  - `assetIp`: IP地址（可选）
  - `systemName`: 系统名称（可选）
  - `organizationName`: 所属单位（可选）
- **响应**: 返回符合条件的资产列表

### 10. 统计资产总数
- **URL**: `GET /api/asset-list/count`
- **权限**: `asset:list`
- **响应**: 返回资产总数

### 11. 检查IP和端口组合是否存在
- **URL**: `GET /api/asset-list/exists?assetIp=192.168.1.100&assetPort=80`
- **权限**: `asset:list`
- **响应**: 返回布尔值

### 12. 导出资产列表
- **URL**: `GET /api/asset-list/download`
- **权限**: `asset:list`
- **响应**: 返回CSV文件

## 数据模型

### AssetListDto
```json
{
    "id": 1,
    "assetIp": "192.168.1.100",
    "assetPort": 80,
    "systemName": "Web服务器",
    "organizationName": "技术部",
    "createdAt": "2025-08-01T10:00:00",
    "updatedAt": "2025-08-01T10:00:00"
}
```

### AssetListQueryDto
```json
{
    "assetIp": "192.168.1",
    "systemName": "Web",
    "organizationName": "技术",
    "page": 1,
    "size": 10,
    "sortBy": "createdAt",
    "sortDirection": "desc"
}
```

## 错误处理
- 400: 请求参数错误
- 401: 未授权
- 403: 权限不足
- 404: 资产不存在
- 409: IP和端口组合已存在
- 500: 服务器内部错误

## 注意事项
1. IP和端口组合必须唯一
2. 端口号必须为非负整数
3. IP地址格式必须正确
4. 创建时间和更新时间由系统自动管理
5. 支持IP地址、系统名称、所属单位的模糊查询 