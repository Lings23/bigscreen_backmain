package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.stat.service.BaseStatService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 统计数据基础服务实现类
 * @param <T> 实体类型
 * @param <R> 仓库类型
 */
public abstract class BaseStatServiceImpl<T, R extends JpaRepository<T, Long>> implements BaseStatService<T> {
    
    protected final R repository;
    
    public BaseStatServiceImpl(R repository) {
        this.repository = repository;
    }
    
    @Override
    public List<T> getAll() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, getDefaultSortField()));
    }
    
    @Override
    public T getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new BadRequestException("未找到ID为" + id + "的记录"));
    }
    
    @Override
    @Transactional
    public T save(T entity) {
        setCreateTime(entity);
        setUpdateTime(entity);
        return repository.save(entity);
    }
    
    @Override
    @Transactional
    public T update(Long id, T entity) {
        Optional<T> optional = repository.findById(id);
        if (optional.isPresent()) {
            T existing = optional.get();
            updateFields(existing, entity);
            setUpdateTime(existing);
            return repository.save(existing);
        } else {
            throw new BadRequestException("未找到ID为" + id + "的记录");
        }
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BadRequestException("未找到ID为" + id + "的记录");
        }
        repository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void deleteAll(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("请选择要删除的记录");
        }
        
        // 验证所有ID是否存在
        for (Long id : ids) {
            if (!repository.existsById(id)) {
                throw new BadRequestException("未找到ID为" + id + "的记录");
            }
        }
        
        // 执行批量删除
        ids.forEach(repository::deleteById);
    }
    
    /**
     * 获取默认排序字段
     * @return 排序字段名称
     */
    protected abstract String getDefaultSortField();
    
    /**
     * 设置创建时间
     * @param entity 实体对象
     */
    protected abstract void setCreateTime(T entity);
    
    /**
     * 设置更新时间
     * @param entity 实体对象
     */
    protected abstract void setUpdateTime(T entity);
    
    /**
     * 更新实体字段
     * @param target 目标实体
     * @param source 源实体
     */
    protected abstract void updateFields(T target, T source);
} 