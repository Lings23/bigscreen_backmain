package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AssetStat;
import me.zhengjie.modules.stat.repository.AssetStatRepository;
import me.zhengjie.modules.stat.service.AssetStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AssetStatServiceImpl extends BaseStatServiceImpl<AssetStat, AssetStatRepository> implements AssetStatService {
    
    @Autowired
    public AssetStatServiceImpl(AssetStatRepository repository) {
        super(repository);
    }

    @Override
    protected String getDefaultSortField() {
        return "statDate";
    }

    @Override
    protected void setCreateTime(AssetStat entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
    }

    @Override
    protected void setUpdateTime(AssetStat entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @Override
    protected void updateFields(AssetStat target, AssetStat source) {
        if (source.getStatDate() != null) {
            target.setStatDate(source.getStatDate());
        }
        if (source.getNetworkDevice() != null) {
            target.setNetworkDevice(source.getNetworkDevice());
        }
        if (source.getSecurityDevice() != null) {
            target.setSecurityDevice(source.getSecurityDevice());
        }
        if (source.getDomainName() != null) {
            target.setDomainName(source.getDomainName());
        }
        if (source.getMiddleware() != null) {
            target.setMiddleware(source.getMiddleware());
        }
        if (source.getService() != null) {
            target.setService(source.getService());
        }
        if (source.getApplication() != null) {
            target.setApplication(source.getApplication());
        }
        if (source.getWebsite() != null) {
            target.setWebsite(source.getWebsite());
        }
        if (source.getVirtualDevice() != null) {
            target.setVirtualDevice(source.getVirtualDevice());
        }
        if (source.getPort() != null) {
            target.setPort(source.getPort());
        }
        if (source.getHost() != null) {
            target.setHost(source.getHost());
        }
        if (source.getDatabaseCount() != null) {
            target.setDatabaseCount(source.getDatabaseCount());
        }
        if (source.getOsCount() != null) {
            target.setOsCount(source.getOsCount());
        }
    }
} 