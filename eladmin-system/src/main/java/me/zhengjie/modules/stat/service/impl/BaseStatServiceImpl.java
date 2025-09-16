package me.zhengjie.modules.stat.service.impl;

import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.stat.service.BaseStatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
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
    
    // ==================== 查询方法 ====================
    
    /**
     * 分页查询所有数据
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    public Page<T> findAll(Integer page, Integer size) {
        validatePaginationParams(page, size);
        Pageable pageable = createPageable(page, size);
        return repository.findAll(pageable);
    }
    
    /**
     * 根据时间范围查询（基于创建时间）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    public Page<T> findByTimePeriod(LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        validateDateRange(startDate, endDate);
        validatePaginationParams(page, size);
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        Pageable pageable = createPageable(page, size);
        
        return findByCreatedAtBetween(startDateTime, endDateTime, pageable);
    }
    
    /**
     * 根据关键字搜索
     * @param keyword 关键字
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    public Page<T> findByKeyword(String keyword, Integer page, Integer size) {
        validatePaginationParams(page, size);
        Pageable pageable = createPageable(page, size);
        
        return findByKeyField(keyword, pageable);
    }
    
    // ==================== 抽象方法 - 子类实现 ====================
    
    /**
     * 根据创建时间范围查询
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页结果
     */
    protected abstract Page<T> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 根据关键字段查询
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 分页结果
     */
    protected abstract Page<T> findByKeyField(String keyword, Pageable pageable);
    
    // ==================== 工具方法 ====================
    
    /**
     * 创建分页对象
     * @param page 页码
     * @param size 页大小
     * @return 分页对象
     */
    public Pageable createPageable(Integer page, Integer size) {
        // 固定页大小为20
        int pageSize = 20;
        Sort sort = Sort.by(Sort.Direction.DESC, getDefaultSortField());
        return PageRequest.of(page, pageSize, sort);
    }
    
    /**
     * 验证分页参数
     * @param page 页码
     * @param size 页大小
     */
    protected void validatePaginationParams(Integer page, Integer size) {
        if (page == null || page < 0) {
            throw new BadRequestException("页码不能为空且必须大于等于0");
        }
        // 忽略用户传入的size参数，固定使用20
    }
    
    /**
     * 验证日期范围
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    protected void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("开始日期和结束日期不能为空");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("开始日期不能晚于结束日期");
        }
        // 限制查询范围不超过1年
        if (startDate.plusYears(1).isBefore(endDate)) {
            throw new BadRequestException("查询时间范围不能超过1年");
        }
    }
} 