package me.zhengjie.modules.stat.service.dto;

import lombok.Data;
import me.zhengjie.base.BaseDTO;
import java.io.Serializable;
import java.sql.Timestamp;

/**
* @description /
* @author ElAdmin
* @date 2025-08-15
**/
@Data
public class TableMetadataDto extends BaseDTO implements Serializable {

    private Long id;

    private String tableName;

    private String columnName;

    private String columnType;

    private Boolean isNullable;

    private String defaultValue;

    private String comment;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}











