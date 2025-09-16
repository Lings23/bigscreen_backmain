package me.zhengjie.modules.stat.service.dto;

import lombok.Data;
import me.zhengjie.annotation.Query;

import java.sql.Timestamp;
import java.util.List;

/**
* @author ElAdmin
* @date 2025-08-15
**/
@Data
public class TableMetadataQueryCriteria{

    @Query(type = Query.Type.IN, propName = "tableName")
    private List<String> tableNames;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createdAt;
}











