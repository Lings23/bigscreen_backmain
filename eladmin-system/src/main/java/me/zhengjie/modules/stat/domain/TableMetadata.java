package me.zhengjie.modules.stat.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
* @description 数据库表字段元数据表
* @author lc
* @date 2025-08-15
**/
@Entity
@Data
@Table(name="table_metadata", uniqueConstraints = {@UniqueConstraint(columnNames={"table_name", "column_name"})})
public class TableMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "column_name", nullable = false)
    private String columnName;

    @Column(name = "column_type", nullable = false)
    private String columnType;

    @Column(name = "is_nullable", nullable = false)
    private Boolean isNullable;

    @Column(name = "default_value")
    private String defaultValue;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    public void copy(TableMetadata source){
        this.tableName = source.tableName;
        this.columnName = source.columnName;
        this.columnType = source.columnType;
        this.isNullable = source.isNullable;
        this.defaultValue = source.defaultValue;
        this.comment = source.comment;
    }
}
