package me.zhengjie.modules.stat.service.mapstruct;

import me.zhengjie.base.BaseMapper;
import me.zhengjie.modules.stat.domain.TableMetadata;
import me.zhengjie.modules.stat.service.dto.TableColumnInfoDto;
import me.zhengjie.modules.stat.service.dto.TableMetadataDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
* @author ElAdmin
* @date 2025-08-15
**/
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TableMetadataMapper extends BaseMapper<TableMetadataDto, TableMetadata> {

    TableColumnInfoDto toColumnInfoDto(TableMetadata tableMetadata);
}
