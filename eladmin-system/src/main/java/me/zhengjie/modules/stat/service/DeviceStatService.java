package me.zhengjie.modules.stat.service;

import me.zhengjie.modules.stat.domain.DeviceStat;
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
} 