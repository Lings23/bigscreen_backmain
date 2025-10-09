package me.zhengjie.modules.maint.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.maint.service.ZeroReportSyncService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ZeroReport值班信息定时同步任务
 * 每天凌晨2点执行一次
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ZeroReportSyncTask {

    private final ZeroReportSyncService zeroReportSyncService;

    /**
     * 每天凌晨2点执行ZeroReport值班信息同步
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncZeroReportDuty() {
        log.info("定时任务触发：开始执行ZeroReport值班信息同步");
        try {
            zeroReportSyncService.syncActiveZeroReportDutyScheduled();
            log.info("定时任务完成：ZeroReport值班信息同步执行完毕");
        } catch (Exception e) {
            log.error("定时任务异常：ZeroReport值班信息同步执行失败", e);
        }
    }

    // /**
    //  * 测试方法：应用启动后延迟10秒执行一次同步（仅用于测试）
    //  */
    // @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    // public void testSyncOnStartup() {
    //     log.info("=== 应用启动测试：开始执行ZeroReport值班信息同步测试 ===");
    //     try {
    //         zeroReportSyncService.syncActiveZeroReportDutyScheduled();
    //         log.info("=== 应用启动测试：ZeroReport值班信息同步测试完成 ===");
    //     } catch (Exception e) {
    //         log.error("=== 应用启动测试：ZeroReport值班信息同步测试失败 ===", e);
    //     }
    // }

    // /**
    //  * 手动测试方法：每5分钟执行一次（仅用于测试）
    //  */
    // @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    // public void testSyncPeriodic() {
    //     log.info("=== 定期测试：开始执行ZeroReport值班信息同步测试 ===");
    //     try {
    //         zeroReportSyncService.syncActiveZeroReportDutyScheduled();
    //         log.info("=== 定期测试：ZeroReport值班信息同步测试完成 ===");
    //     } catch (Exception e) {
    //         log.error("=== 定期测试：ZeroReport值班信息同步测试失败 ===", e);
    //     }
    // }
}
