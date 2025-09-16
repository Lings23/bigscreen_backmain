package me.zhengjie.modules.stat.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.stat.domain.TableMetadata;
import me.zhengjie.modules.stat.service.TableMetadataService;
import me.zhengjie.modules.stat.service.dto.TableMetadataQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author ElAdmin
* @date 2025-08-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "stat:tableMetadata管理")
@RequestMapping("/api/tableMetadata")
public class TableMetadataController {

    private final TableMetadataService tableMetadataService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('tableMetadata:list')")
    public void exportTableMetadata(HttpServletResponse response, TableMetadataQueryCriteria criteria) throws IOException {
        tableMetadataService.download(tableMetadataService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询数据库表字段元数据表")
    @ApiOperation("查询数据库表字段元数据表")
    @PreAuthorize("@el.check('tableMetadata:list')")
    public ResponseEntity<Object> queryTableMetadata(TableMetadataQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(tableMetadataService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/columns/{tableName}")
    @Log("根据表名查询列信息")
    @ApiOperation("根据表名查询列信息")
    @PreAuthorize("@el.check('tableMetadata:list')")
    public ResponseEntity<Object> queryColumnsByTableName(@PathVariable String tableName){
        return new ResponseEntity<>(tableMetadataService.findByTableName(tableName), HttpStatus.OK);
    }

    @PostMapping
    @Log("新增数据库表字段元数据表")
    @ApiOperation("新增数据库表字段元数据表")
    @PreAuthorize("@el.check('tableMetadata:add')")
    public ResponseEntity<Object> createTableMetadata(@Validated @RequestBody TableMetadata resources){
        return new ResponseEntity<>(tableMetadataService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改数据库表字段元数据表")
    @ApiOperation("修改数据库表字段元数据表")
    @PreAuthorize("@el.check('tableMetadata:edit')")
    public ResponseEntity<Object> updateTableMetadata(@Validated @RequestBody TableMetadata resources){
        tableMetadataService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除数据库表字段元数据表")
    @ApiOperation("删除数据库表字段元数据表")
    @PreAuthorize("@el.check('tableMetadata:del')")
    public ResponseEntity<Object> deleteTableMetadata(@RequestBody Long[] ids) {
        tableMetadataService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
