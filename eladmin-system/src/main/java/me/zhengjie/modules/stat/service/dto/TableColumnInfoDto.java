package me.zhengjie.modules.stat.service.dto;

import lombok.Data;
import java.io.Serializable;

/**
* @description 用于根据表名查询时返回列信息的 DTO
* @author ElAdmin
* @date 2025-08-15
**/
@Data
public class TableColumnInfoDto implements Serializable {

    private String columnName;

    private String columnType;

    private Boolean isNullable;
}











