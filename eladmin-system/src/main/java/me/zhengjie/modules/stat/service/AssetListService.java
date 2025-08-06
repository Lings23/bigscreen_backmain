package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.dto.AssetListDto;
import me.zhengjie.modules.stat.dto.AssetListQueryDto;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 资产列表Service接口
 * @author system
 * @date 2025-08-01
 */
public interface AssetListService {

    /**
     * 创建资产
     * @param assetListDto 资产信息
     * @return 创建的资产信息
     */
    AssetListDto createAsset(AssetListDto assetListDto);

    /**
     * 更新资产
     * @param id 资产ID
     * @param assetListDto 资产信息
     * @return 更新后的资产信息
     */
    AssetListDto updateAsset(Long id, AssetListDto assetListDto);

    /**
     * 删除资产
     * @param id 资产ID
     */
    void deleteAsset(Long id);

    /**
     * 批量删除资产
     * @param ids 资产ID列表
     */
    void deleteAssets(List<Long> ids);

    /**
     * 根据ID获取资产
     * @param id 资产ID
     * @return 资产信息
     */
    AssetListDto getAssetById(Long id);

    /**
     * 根据IP和端口获取资产
     * @param assetIp 资产IP
     * @param assetPort 资产端口
     * @return 资产信息
     */
    AssetListDto getAssetByIpAndPort(String assetIp, Integer assetPort);

    /**
     * 分页查询资产列表
     * @param queryDto 查询条件
     * @return 分页结果
     */
    Page<AssetListDto> getAssetList(AssetListQueryDto queryDto);

    /**
     * 获取所有资产列表
     * @return 资产列表
     */
    List<AssetListDto> getAllAssets();

    /**
     * 根据条件查询资产列表
     * @param assetIp IP地址（可选）
     * @param systemName 系统名称（可选）
     * @param organizationName 所属单位（可选）
     * @return 资产列表
     */
    List<AssetListDto> getAssetsByConditions(String assetIp, String systemName, String organizationName);

    /**
     * 统计资产总数
     * @return 资产总数
     */
    long countAssets();

    /**
     * 检查IP和端口组合是否存在
     * @param assetIp 资产IP
     * @param assetPort 资产端口
     * @return 是否存在
     */
    boolean existsByIpAndPort(String assetIp, Integer assetPort);

    /**
     * 导出资产列表
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    void download(HttpServletResponse response) throws IOException;
} 