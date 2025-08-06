package me.zhengjie.modules.stat.service.impl;

import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.exception.EntityNotFoundException;
import me.zhengjie.modules.stat.domain.AssetList;
import me.zhengjie.modules.stat.dto.AssetListDto;
import me.zhengjie.modules.stat.dto.AssetListQueryDto;
import me.zhengjie.modules.stat.repository.AssetListRepository;
import me.zhengjie.modules.stat.service.AssetListService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产列表Service实现类
 * @author system
 * @date 2025-08-01
 */
@Service
@RequiredArgsConstructor
public class AssetListServiceImpl implements AssetListService {

    private final AssetListRepository assetListRepository;

    @Override
    @Transactional
    public AssetListDto createAsset(AssetListDto assetListDto) {
        // 检查IP和端口组合是否已存在
        if (existsByIpAndPort(assetListDto.getAssetIp(), assetListDto.getAssetPort())) {
            throw new EntityExistException(AssetList.class, "IP和端口", assetListDto.getAssetIp() + ":" + assetListDto.getAssetPort());
        }

        AssetList assetList = new AssetList();
        BeanUtils.copyProperties(assetListDto, assetList);
        
        AssetList savedAsset = assetListRepository.save(assetList);
        AssetListDto result = new AssetListDto();
        BeanUtils.copyProperties(savedAsset, result);
        
        return result;
    }

    @Override
    @Transactional
    public AssetListDto updateAsset(Long id, AssetListDto assetListDto) {
        AssetList existingAsset = assetListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AssetList.class, "ID", id.toString()));

        // 检查IP和端口组合是否与其他记录冲突
        if (!existingAsset.getAssetIp().equals(assetListDto.getAssetIp()) || 
            !existingAsset.getAssetPort().equals(assetListDto.getAssetPort())) {
            if (existsByIpAndPort(assetListDto.getAssetIp(), assetListDto.getAssetPort())) {
                throw new EntityExistException(AssetList.class, "IP和端口", assetListDto.getAssetIp() + ":" + assetListDto.getAssetPort());
            }
        }

        BeanUtils.copyProperties(assetListDto, existingAsset, "id", "createdAt");
        
        AssetList savedAsset = assetListRepository.save(existingAsset);
        AssetListDto result = new AssetListDto();
        BeanUtils.copyProperties(savedAsset, result);
        
        return result;
    }

    @Override
    @Transactional
    public void deleteAsset(Long id) {
        if (!assetListRepository.existsById(id)) {
            throw new EntityNotFoundException(AssetList.class, "ID", id.toString());
        }
        assetListRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteAssets(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("删除ID列表不能为空");
        }
        assetListRepository.deleteAllById(ids);
    }

    @Override
    public AssetListDto getAssetById(Long id) {
        AssetList assetList = assetListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AssetList.class, "ID", id.toString()));
        
        AssetListDto result = new AssetListDto();
        BeanUtils.copyProperties(assetList, result);
        return result;
    }

    @Override
    public AssetListDto getAssetByIpAndPort(String assetIp, Integer assetPort) {
        AssetList assetList = assetListRepository.findByAssetIpAndAssetPort(assetIp, assetPort)
                .orElseThrow(() -> new EntityNotFoundException(AssetList.class, "IP和端口", assetIp + ":" + assetPort));
        
        AssetListDto result = new AssetListDto();
        BeanUtils.copyProperties(assetList, result);
        return result;
    }

    @Override
    public Page<AssetListDto> getAssetList(AssetListQueryDto queryDto) {
        // 构建分页和排序
        Sort sort = Sort.by(Sort.Direction.fromString(queryDto.getSortDirection()), queryDto.getSortBy());
        Pageable pageable = PageRequest.of(queryDto.getPage() - 1, queryDto.getSize(), sort);

        // 构建查询条件
        Specification<AssetList> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(queryDto.getAssetIp())) {
                predicates.add(criteriaBuilder.like(root.get("assetIp"), "%" + queryDto.getAssetIp() + "%"));
            }

            if (StringUtils.hasText(queryDto.getSystemName())) {
                predicates.add(criteriaBuilder.like(root.get("systemName"), "%" + queryDto.getSystemName() + "%"));
            }

            if (StringUtils.hasText(queryDto.getOrganizationName())) {
                predicates.add(criteriaBuilder.like(root.get("organizationName"), "%" + queryDto.getOrganizationName() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<AssetList> assetPage = assetListRepository.findAll(spec, pageable);
        
        return assetPage.map(assetList -> {
            AssetListDto dto = new AssetListDto();
            BeanUtils.copyProperties(assetList, dto);
            return dto;
        });
    }

    @Override
    public List<AssetListDto> getAllAssets() {
        List<AssetList> assetList = assetListRepository.findAll();
        return assetList.stream().map(asset -> {
            AssetListDto dto = new AssetListDto();
            BeanUtils.copyProperties(asset, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AssetListDto> getAssetsByConditions(String assetIp, String systemName, String organizationName) {
        List<AssetList> assetList = assetListRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(assetIp)) {
                predicates.add(criteriaBuilder.like(root.get("assetIp"), "%" + assetIp + "%"));
            }

            if (StringUtils.hasText(systemName)) {
                predicates.add(criteriaBuilder.like(root.get("systemName"), "%" + systemName + "%"));
            }

            if (StringUtils.hasText(organizationName)) {
                predicates.add(criteriaBuilder.like(root.get("organizationName"), "%" + organizationName + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        return assetList.stream().map(asset -> {
            AssetListDto dto = new AssetListDto();
            BeanUtils.copyProperties(asset, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public long countAssets() {
        return assetListRepository.countAssets();
    }

    @Override
    public boolean existsByIpAndPort(String assetIp, Integer assetPort) {
        return assetListRepository.existsByAssetIpAndAssetPort(assetIp, assetPort);
    }

    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<AssetListDto> assetList = getAllAssets();
        
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=asset_list.csv");
        
        // 写入CSV头部
        response.getWriter().write("资产IP,资产端口,系统名称,所属单位,创建时间,更新时间\n");
        
        // 写入数据
        for (AssetListDto asset : assetList) {
            response.getWriter().write(String.format("\"%s\",%d,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    asset.getAssetIp(),
                    asset.getAssetPort(),
                    asset.getSystemName() != null ? asset.getSystemName() : "",
                    asset.getOrganizationName() != null ? asset.getOrganizationName() : "",
                    asset.getCreatedAt() != null ? asset.getCreatedAt().toString() : "",
                    asset.getUpdatedAt() != null ? asset.getUpdatedAt().toString() : ""));
        }
        
        response.getWriter().flush();
    }
} 