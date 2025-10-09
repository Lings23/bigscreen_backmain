package me.zhengjie.modules.stat.repository;

import me.zhengjie.modules.stat.domain.AssetList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 资产列表Repository接口
 * @author system
 * @date 2025-08-01
 */
@Repository
public interface AssetListRepository extends JpaRepository<AssetList, Long>, JpaSpecificationExecutor<AssetList> {

    /**
     * 根据IP和端口查找资产
     * @param assetIp 资产IP
     * @param assetPort 资产端口
     * @return 资产信息
     */
    Optional<AssetList> findByAssetIpAndAssetPort(String assetIp, Integer assetPort);

    /**
     * 根据IP地址查找资产列表
     * @param assetIp 资产IP
     * @return 资产列表
     */
    List<AssetList> findByAssetIpContaining(String assetIp);

    /**
     * 根据系统名称查找资产列表
     * @param systemName 系统名称
     * @return 资产列表
     */
    List<AssetList> findBySystemNameContaining(String systemName);

    /**
     * 根据所属单位查找资产列表
     * @param organizationName 所属单位
     * @return 资产列表
     */
    List<AssetList> findByOrganizationNameContaining(String organizationName);

    /**
     * 根据关键字搜索资产列表（支持IP、系统名称、所属单位的模糊查询，忽略大小写）
     * @param assetIp 资产IP关键字
     * @param systemName 系统名称关键字
     * @param organizationName 所属单位关键字
     * @param pageable 分页参数
     * @return 资产列表分页结果
     */
    Page<AssetList> findByAssetIpContainingIgnoreCaseOrSystemNameContainingIgnoreCaseOrOrganizationNameContainingIgnoreCase(
        String assetIp, String systemName, String organizationName, Pageable pageable);

    /**
     * 检查IP和端口组合是否存在
     * @param assetIp 资产IP
     * @param assetPort 资产端口
     * @return 是否存在
     */
    boolean existsByAssetIpAndAssetPort(String assetIp, Integer assetPort);

    Page<AssetList> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    /**
     * 统计资产总数
     * @return 资产总数
     */
    @Query("SELECT COUNT(a) FROM AssetList a")
    long countAssets();

    /**
     * 根据条件统计资产数量
     * @param assetIp IP地址（可选）
     * @param systemName 系统名称（可选）
     * @param organizationName 所属单位（可选）
     * @return 资产数量
     */
    @Query("SELECT COUNT(a) FROM AssetList a WHERE " +
           "(:assetIp IS NULL OR a.assetIp LIKE %:assetIp%) AND " +
           "(:systemName IS NULL OR a.systemName LIKE %:systemName%) AND " +
           "(:organizationName IS NULL OR a.organizationName LIKE %:organizationName%)")
    long countByConditions(@Param("assetIp") String assetIp,
                          @Param("systemName") String systemName,
                          @Param("organizationName") String organizationName);
} 