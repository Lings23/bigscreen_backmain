package me.zhengjie.modules.stat.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.stat.domain.TableMetadata;
import me.zhengjie.modules.stat.repository.TableMetadataRepository;
import me.zhengjie.modules.stat.service.TableMetadataService;
import me.zhengjie.modules.stat.service.dto.TableMetadataDto;
import me.zhengjie.modules.stat.service.dto.TableMetadataQueryCriteria;
import me.zhengjie.modules.stat.service.mapstruct.TableMetadataMapper;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ValidationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.zhengjie.modules.stat.service.dto.TableColumnInfoDto;

/**
* @author ElAdmin
* @date 2025-08-15
**/
@Service
@RequiredArgsConstructor
public class TableMetadataServiceImpl implements TableMetadataService {

    private final TableMetadataRepository tableMetadataRepository;
    private final TableMetadataMapper tableMetadataMapper;

    @Override
    public Map<String,Object> queryAll(TableMetadataQueryCriteria criteria, Pageable pageable){
        Page<TableMetadata> page = tableMetadataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", tableMetadataMapper.toDto(page.getContent()));
        map.put("totalElements", page.getTotalElements());
        return map;
    }

    @Override
    public List<TableMetadataDto> queryAll(TableMetadataQueryCriteria criteria){
        return tableMetadataMapper.toDto(tableMetadataRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    public List<TableColumnInfoDto> findByTableName(String tableName) {
        return tableMetadataRepository.findByTableName(tableName).stream()
                .map(tableMetadataMapper::toColumnInfoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableMetadataDto findById(Long id) {
        TableMetadata tableMetadata = tableMetadataRepository.findById(id).orElseGet(TableMetadata::new);
        ValidationUtil.isNull(tableMetadata.getId(),"TableMetadata","id",id);
        return tableMetadataMapper.toDto(tableMetadata);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TableMetadataDto create(TableMetadata resources) {
        return tableMetadataMapper.toDto(tableMetadataRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TableMetadata resources) {
        TableMetadata tableMetadata = tableMetadataRepository.findById(resources.getId()).orElseGet(TableMetadata::new);
        ValidationUtil.isNull( tableMetadata.getId(),"TableMetadata","id",resources.getId());
        tableMetadata.copy(resources);
        tableMetadataRepository.save(tableMetadata);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            tableMetadataRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<TableMetadataDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TableMetadataDto tableMetadata : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("tableName", tableMetadata.getTableName());
            map.put("columnName", tableMetadata.getColumnName());
            map.put("columnType", tableMetadata.getColumnType());
            map.put("isNullable", tableMetadata.getIsNullable());
            map.put("defaultValue", tableMetadata.getDefaultValue());
            map.put("comment", tableMetadata.getComment());
            map.put("createdAt", tableMetadata.getCreatedAt());
            map.put("updatedAt", tableMetadata.getUpdatedAt());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
