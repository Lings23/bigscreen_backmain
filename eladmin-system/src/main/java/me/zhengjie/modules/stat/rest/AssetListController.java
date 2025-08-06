package me.zhengjie.modules.stat.rest;

import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.stat.dto.AssetListDto;
import me.zhengjie.modules.stat.dto.AssetListQueryDto;
import me.zhengjie.modules.stat.service.AssetListService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 资产列表Controller
 * @author system
 * @date 2025-08-01
 */
@RestController
@RequestMapping("/api/asset-list")
@RequiredArgsConstructor
public class AssetListController {

    private final AssetListService assetListService;

    /**
     * 创建资产
     */
    @Log("创建资产")
    @PostMapping
    @PreAuthorize("@el.check('asset:add')")
    public ResponseEntity<AssetListDto> createAsset(@Validated @RequestBody AssetListDto assetListDto) {
        AssetListDto result = assetListService.createAsset(assetListDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 更新资产
     */
    @Log("更新资产")
    @PutMapping("/{id}")
    @PreAuthorize("@el.check('asset:edit')")
    public ResponseEntity<AssetListDto> updateAsset(@PathVariable Long id, @Validated @RequestBody AssetListDto assetListDto) {
        AssetListDto result = assetListService.updateAsset(id, assetListDto);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除资产
     */
    @Log("删除资产")
    @DeleteMapping("/{id}")
    @PreAuthorize("@el.check('asset:del')")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        assetListService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量删除资产
     */
    @Log("批量删除资产")
    @DeleteMapping
    @PreAuthorize("@el.check('asset:del')")
    public ResponseEntity<Void> deleteAssets(@RequestBody List<Long> ids) {
        assetListService.deleteAssets(ids);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据ID获取资产
     */
    @GetMapping("/{id}")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<AssetListDto> getAssetById(@PathVariable Long id) {
        AssetListDto result = assetListService.getAssetById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据IP和端口获取资产
     */
    @GetMapping("/ip-port")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<AssetListDto> getAssetByIpAndPort(@RequestParam String assetIp, @RequestParam Integer assetPort) {
        AssetListDto result = assetListService.getAssetByIpAndPort(assetIp, assetPort);
        return ResponseEntity.ok(result);
    }

    /**
     * 分页查询资产列表
     */
    @GetMapping
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<Page<AssetListDto>> getAssetList(AssetListQueryDto queryDto) {
        Page<AssetListDto> result = assetListService.getAssetList(queryDto);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取所有资产列表
     */
    @GetMapping("/all")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<List<AssetListDto>> getAllAssets() {
        List<AssetListDto> result = assetListService.getAllAssets();
        return ResponseEntity.ok(result);
    }

    /**
     * 根据条件查询资产列表
     */
    @GetMapping("/search")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<List<AssetListDto>> getAssetsByConditions(
            @RequestParam(required = false) String assetIp,
            @RequestParam(required = false) String systemName,
            @RequestParam(required = false) String organizationName) {
        List<AssetListDto> result = assetListService.getAssetsByConditions(assetIp, systemName, organizationName);
        return ResponseEntity.ok(result);
    }

    /**
     * 统计资产总数
     */
    @GetMapping("/count")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<Long> countAssets() {
        long count = assetListService.countAssets();
        return ResponseEntity.ok(count);
    }

    /**
     * 检查IP和端口组合是否存在
     */
    @GetMapping("/exists")
    @PreAuthorize("@el.check('asset:list')")
    public ResponseEntity<Boolean> existsByIpAndPort(@RequestParam String assetIp, @RequestParam Integer assetPort) {
        boolean exists = assetListService.existsByIpAndPort(assetIp, assetPort);
        return ResponseEntity.ok(exists);
    }

    /**
     * 导出资产列表
     */
    @Log("导出资产列表")
    @GetMapping("/download")
    @PreAuthorize("@el.check('asset:list')")
    public void download(HttpServletResponse response) throws IOException {
        assetListService.download(response);
    }
} 