package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.TableMetadata;
import me.zhengjie.modules.stat.service.dto.TableMetadataDto;
import me.zhengjie.modules.stat.service.dto.TableMetadataQueryCriteria;
import org.springframework.data.domain.Pageable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import me.zhengjie.modules.stat.service.dto.TableColumnInfoDto;

/**
* @author ElAdmin
* @date 2025-08-15
**/
public interface TableMetadataService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(TableMetadataQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件
    * @return List<TableMetadataDto>
    */
    List<TableMetadataDto> queryAll(TableMetadataQueryCriteria criteria);

    /**
     * 根据表名查询列信息
     * @param tableName 表名
     * @return /
     */
    List<TableColumnInfoDto> findByTableName(String tableName);

    /**
     * 根据ID查询
     * @param id ID
     * @return TableMetadataDto
     */
    TableMetadataDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return TableMetadataDto
    */
    TableMetadataDto create(TableMetadata resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(TableMetadata resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Long[] ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<TableMetadataDto> all, HttpServletResponse response) throws IOException;
}
