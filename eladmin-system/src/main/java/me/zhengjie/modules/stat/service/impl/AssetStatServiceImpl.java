package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.modules.stat.domain.AssetStat;
import me.zhengjie.modules.stat.dto.AssetStatQueryCriteria;
import me.zhengjie.modules.stat.repository.AssetStatRepository;
import me.zhengjie.modules.stat.service.AssetStatService;
import me.zhengjie.modules.stat.specification.AssetStatSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    protected Page<AssetStat> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return repository.findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    protected Page<AssetStat> findByKeyField(String keyword, Pageable pageable) {
        // For AssetStat, we can search by stat date as the key field
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }
        // Try to parse as date string and search by stat date
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(keyword);
            return repository.findByStatDate(date, pageable);
        } catch (Exception e) {
            return repository.findAll(pageable);
        }
    }
    
    @Override
    public Page<AssetStat> findAll(Specification<AssetStat> spec, Pageable pageable) {
        return repository.findAll(spec, pageable);
    }

    @Override
    public Page<AssetStat> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size) {
        Pageable pageable = createPageable(page, size);
        return findByCreatedAtBetween(startTime, endTime, pageable);
    }

    @Override
    public Page<AssetStat> findByKeyword(String keyword, Integer page, Integer size) {
        Pageable pageable = createPageable(page, size);
        return findByKeyField(keyword, pageable);
    }

    @Override
    public Page<AssetStat> findByCriteria(AssetStatQueryCriteria criteria, Pageable pageable) {
        Specification<AssetStat> spec = AssetStatSpecification.createSpecification(criteria);
        return findAll(spec, pageable);
    }

    @Override
    public Pageable createPageable(Integer page, Integer size) {
        return super.createPageable(page, size);
    }
}