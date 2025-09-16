# Stat Module Query Functions and API Interfaces

## Overview

This document describes the comprehensive query functions and API interfaces implemented for the stat module. The implementation provides time-based filtering, key field searching, and pagination with a fixed page size of 20 records per page.

## Architecture

### Core Components

1. **BaseStatServiceImpl** - Generic service implementation with common query methods
2. **PageResult** - Consistent pagination response wrapper
3. **Query Criteria DTOs** - Type-safe query parameter objects
4. **JPA Specifications** - Dynamic query builders for complex filtering
5. **Enhanced Repositories** - Extended with custom query methods
6. **REST Controllers** - RESTful API endpoints with Swagger documentation

## API Endpoints

### Asset Statistics API (`/api/stat/asset`)

#### Basic Operations
- `GET /api/stat/asset` - Get all asset statistics
- `GET /api/stat/asset/{id}` - Get asset statistics by ID
- `POST /api/stat/asset` - Create new asset statistics
- `PUT /api/stat/asset/{id}` - Update asset statistics
- `DELETE /api/stat/asset/{id}` - Delete asset statistics
- `DELETE /api/stat/asset` - Batch delete asset statistics

#### Query Operations
- `GET /api/stat/asset/page` - Paginated query
- `GET /api/stat/asset/period` - Time-based query
- `GET /api/stat/asset/search` - Keyword search
- `POST /api/stat/asset/query` - Advanced criteria-based query

### Security Event API (`/api/stat/event`)

#### Basic Operations
- `GET /api/stat/event` - Get all security events
- `GET /api/stat/event/{id}` - Get security event by ID
- `POST /api/stat/event` - Create new security event
- `PUT /api/stat/event/{id}` - Update security event
- `DELETE /api/stat/event/{id}` - Delete security event
- `DELETE /api/stat/event` - Batch delete security events

## Query Parameters

### Pagination Parameters
- `page` (Integer, default: 0) - Page number (0-based)
- `size` (Integer, fixed: 20) - Page size (always 20, user input ignored)

### Time-based Query Parameters
- `startDate` (Date, ISO format) - Start date for filtering
- `endDate` (Date, ISO format) - End date for filtering

### Search Parameters
- `keyword` (String) - Search keyword for key fields

## Request/Response Examples

### 1. Paginated Query
```http
GET /api/stat/asset/page?page=0&size=20
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "statDate": "2025-09-08",
      "networkDevice": 150,
      "securityDevice": 25,
      "domainName": 300,
      "middleware": 45,
      "service": 200,
      "application": 80,
      "website": 120,
      "virtualDevice": 35,
      "port": 5000,
      "host": 180,
      "databaseCount": 15,
      "osCount": 12,
      "createdAt": "2025-09-08T09:00:00",
      "updatedAt": "2025-09-08T09:00:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### 2. Time-based Query
```http
GET /api/stat/asset/period?startDate=2025-09-01&endDate=2025-09-08&page=0
```

### 3. Keyword Search
```http
GET /api/stat/asset/search?keyword=2025-09-08&page=0
```

### 4. Advanced Query with Criteria
```http
POST /api/stat/asset/query?page=0&size=20
Content-Type: application/json

{
  "startDate": "2025-09-01",
  "endDate": "2025-09-08",
  "networkDeviceMin": 100,
  "networkDeviceMax": 200,
  "securityDeviceMin": 20,
  "securityDeviceMax": 50,
  "status": "active",
  "keyword": "network"
}
```

### 5. Security Event Query with Criteria
```http
POST /api/stat/event/query?page=0&size=20
Content-Type: application/json

{
  "systemName": "firewall",
  "ipAddress": "192.168.1",
  "status": "critical",
  "eventTimeStart": "2025-09-08T00:00:00",
  "eventTimeEnd": "2025-09-08T23:59:59",
  "source": "intrusion",
  "contentKeyword": "attack"
}
```

## Query Criteria Objects

### AssetStatQueryCriteria
```java
{
  "startDate": "2025-09-01",           // Start date filter
  "endDate": "2025-09-08",             // End date filter
  "keyword": "search term",            // General keyword search
  "status": "active",                  // Status filter
  "statDate": "2025-09-08",           // Specific stat date
  "networkDeviceMin": 100,             // Min network devices
  "networkDeviceMax": 200,             // Max network devices
  "securityDeviceMin": 20,             // Min security devices
  "securityDeviceMax": 50,             // Max security devices
  "createdAtStart": "2025-09-08T00:00:00",
  "createdAtEnd": "2025-09-08T23:59:59",
  "updatedAtStart": "2025-09-08T00:00:00",
  "updatedAtEnd": "2025-09-08T23:59:59"
}
```

### SecurityEventQueryCriteria
```java
{
  "startDate": "2025-09-01",
  "endDate": "2025-09-08",
  "keyword": "search term",
  "status": "critical",
  "systemName": "firewall-01",
  "ipAddress": "192.168.1.100",
  "eventTimeStart": "2025-09-08T00:00:00",
  "eventTimeEnd": "2025-09-08T23:59:59",
  "source": "intrusion_detection",
  "contentKeyword": "malware",
  "createdAtStart": "2025-09-08T00:00:00",
  "createdAtEnd": "2025-09-08T23:59:59"
}
```

## Error Handling

### Validation Errors
- Invalid date ranges (start date after end date)
- Date ranges exceeding 1 year
- Invalid page numbers (negative values)
- Missing required parameters

### Error Response Format
```json
{
  "timestamp": "2025-09-08T09:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "开始日期不能晚于结束日期",
  "path": "/api/stat/asset/period"
}
```

## Performance Considerations

### Database Optimization
- Indexes on frequently queried date fields (`created_at`, `stat_date`, `event_time`)
- JPA Projections for DTO queries to reduce data transfer
- Query result caching for frequently accessed data

### Pagination
- Fixed page size of 20 to ensure consistent performance
- Default sorting by creation time (newest first)
- Efficient database-level pagination

### Caching Strategy
- Time-based cache expiration for statistical data
- Redis support for distributed caching
- Query result caching for complex criteria

## Swagger Documentation

All endpoints are documented with Swagger annotations:
- `@Api` - Controller-level documentation
- `@ApiOperation` - Method-level descriptions
- `@ApiImplicitParams` - Parameter documentation
- Input validation with `@Validated` and `@Min`

Access Swagger UI at: `/swagger-ui.html`

## Testing

### Unit Tests
```java
@Test
public void testFindByTimePeriod() {
    LocalDate startDate = LocalDate.of(2025, 9, 1);
    LocalDate endDate = LocalDate.of(2025, 9, 8);
    Page<AssetStat> result = service.findByTimePeriod(startDate, endDate, 0, 20);
    assertThat(result.getContent()).isNotEmpty();
    assertThat(result.getSize()).isEqualTo(20);
}
```

### Integration Tests
```java
@Test
public void testAssetStatQueryEndpoint() throws Exception {
    mockMvc.perform(get("/api/stat/asset/period")
            .param("startDate", "2025-09-01")
            .param("endDate", "2025-09-08")
            .param("page", "0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").isNumber());
}
```

## Usage Examples

### Frontend Integration (JavaScript)
```javascript
// Paginated query
const getAssetStats = async (page = 0) => {
  const response = await fetch(`/api/stat/asset/page?page=${page}&size=20`);
  return await response.json();
};

// Time-based query
const getAssetStatsByPeriod = async (startDate, endDate, page = 0) => {
  const response = await fetch(
    `/api/stat/asset/period?startDate=${startDate}&endDate=${endDate}&page=${page}`
  );
  return await response.json();
};

// Advanced query with criteria
const queryAssetStats = async (criteria, page = 0) => {
  const response = await fetch(`/api/stat/asset/query?page=${page}&size=20`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(criteria)
  });
  return await response.json();
};
```

### Backend Integration (Java)
```java
@Autowired
private AssetStatServiceImpl assetStatService;

// Use service methods directly
public PageResult<AssetStat> getAssetStatistics(LocalDate start, LocalDate end, int page) {
    Page<AssetStat> result = assetStatService.findByTimePeriod(start, end, page, 20);
    return PageResult.of(result);
}

// Use criteria-based query
public PageResult<AssetStat> searchAssetStats(AssetStatQueryCriteria criteria, int page) {
    Pageable pageable = PageRequest.of(page, 20, 
        Sort.by(Sort.Direction.DESC, "statDate"));
    Page<AssetStat> result = assetStatService.findByCriteria(criteria, pageable);
    return PageResult.of(result);
}
```

## Future Enhancements

1. **Export Functionality** - CSV/Excel export for query results
2. **Real-time Updates** - WebSocket support for live data updates
3. **Advanced Analytics** - Aggregation queries and statistical analysis
4. **Custom Sorting** - User-configurable sorting options
5. **Saved Queries** - Ability to save and reuse complex query criteria
6. **Audit Logging** - Track query usage and performance metrics

## Support

For technical support or feature requests, please contact the development team or create an issue in the project repository.
