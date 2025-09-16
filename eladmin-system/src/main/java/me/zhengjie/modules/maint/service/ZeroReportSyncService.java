package me.zhengjie.modules.maint.service;

import java.util.Map;

/**
 * 同步远程数据库的重保事件 ZeroReport 相关值班信息到本地 duty_schedule
 * 定时任务服务，每天自动执行一次
 */
public interface ZeroReportSyncService {

    /**
     * 定时任务：从远程数据库读取当前 status=1 的 ZeroReport 对应的全部值班信息，
     * 按 dept_id + work_date 合并 leader/operator 记录，并插入到本地 duty_schedule。
     * 此方法由定时任务调用，无需参数，自动遍历所有配置的远程数据库
     */
    void syncActiveZeroReportDutyScheduled();

    /**
     * 从指定远程数据库同步值班信息（内部方法）
     *
     * @param databaseId 远程数据库在 mnt_database 的 ID
     * @return 同步结果统计
     */
    Map<String, Object> syncActiveZeroReportDuty(String databaseId);
}
