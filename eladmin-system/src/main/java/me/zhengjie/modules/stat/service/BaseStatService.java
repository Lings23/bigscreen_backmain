package me.zhengjie.modules.stat.service;

import java.util.List;
import java.util.Set;

/**
 * 统计数据基础服务接口
 * @param <T> 实体类型
 */
public interface BaseStatService<T> {
    
    /**
     * 获取所有数据
     * @return 数据列表
     */
    List<T> getAll();
    
    /**
     * 根据ID获取数据
     * @param id 主键ID
     * @return 单条数据
     */
    T getById(Long id);
    
    /**
     * 保存数据
     * @param entity 实体对象
     * @return 保存后的实体对象
     */
    T save(T entity);
    
    /**
     * 更新数据
     * @param id 主键ID
     * @param entity 实体对象
     * @return 更新后的实体对象
     */
    T update(Long id, T entity);
    
    /**
     * 删除数据
     * @param id 主键ID
     */
    void delete(Long id);
    
    /**
     * 批量删除数据
     * @param ids 主键ID集合
     */
    void deleteAll(Set<Long> ids);
} 