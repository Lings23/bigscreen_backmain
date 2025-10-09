package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.DeviceStat;
import me.zhengjie.modules.stat.dto.DeviceStatQueryCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.List;

public interface DeviceStatService extends BaseStatService<DeviceStat> {
    /**
     * 按时间范围查询设备状态统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据列表
     */
    List<DeviceStat> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 标准CRUD查询 - 支持动态Specification和Pageable
     */
    Page<DeviceStat> findAll(Specification<DeviceStat> spec, Pageable pageable);
    
    /**
     * 分页查询所有数据
     */
    Page<DeviceStat> findAll(Integer page, Integer size);
    
    /**
     * 根据时间范围查询
     */
    Page<DeviceStat> findByTimePeriod(LocalDateTime startTime, LocalDateTime endTime, Integer page, Integer size);
    
    /**
     * 根据关键字搜索
     */
    Page<DeviceStat> findByKeyword(String keyword, Integer page, Integer size);
    
    /**
     * 复杂条件查询
     */
    Page<DeviceStat> findByCriteria(DeviceStatQueryCriteria criteria, Pageable pageable);
    
    /**
     * 创建分页对象
     */
    Pageable createPageable(Integer page, Integer size);
}