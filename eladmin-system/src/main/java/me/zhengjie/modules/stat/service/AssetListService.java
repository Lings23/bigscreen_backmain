package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.AssetList;
import me.zhengjie.modules.stat.dto.AssetListDto;
import me.zhengjie.modules.stat.dto.AssetListQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
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

    // 标准CRUD分页查询接口方法
    /**
     * 根据Specification和Pageable查询
     * @param spec 查询条件
     * @param pageable 分页信息
     * @return 分页结果
     */
    Page<AssetList> findAll(Specification<AssetList> spec, Pageable pageable);

    /**
     * 根据时间段查询
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<AssetList> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);

    /**
     * 根据关键字查询
     * @param keyword 关键字
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    Page<AssetList> findByKeyword(String keyword, Integer page, Integer size);

    /**
     * 创建分页对象
     * @param page 页码
     * @param size 每页大小
     * @return 分页对象
     */
    Pageable createPageable(Integer page, Integer size);
} 