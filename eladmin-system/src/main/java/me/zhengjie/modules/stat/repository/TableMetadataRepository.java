package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.TableMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
* @author ElAdmin
* @date 2025-08-15
**/
public interface TableMetadataRepository extends JpaRepository<TableMetadata, Long>, JpaSpecificationExecutor<TableMetadata> {
    /**
    * 根据表名查询
    * @param tableName 表名
    * @return /
    */
    List<TableMetadata> findByTableName(String tableName);
}
