package me.zhengjie.modules.maint.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.maint.service.ZeroReportSyncService;
import me.zhengjie.modules.maint.service.DatabaseService;
import me.zhengjie.modules.maint.service.dto.DatabaseDto;
import me.zhengjie.modules.maint.service.dto.DatabaseQueryCriteria;
import me.zhengjie.modules.stat.domain.DutySchedule;
import me.zhengjie.modules.stat.repository.DutyScheduleRepository;
import me.zhengjie.utils.ValidationUtil;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZeroReportSyncServiceImpl implements ZeroReportSyncService {

    private final DatabaseService databaseService;
    private final DutyScheduleRepository dutyScheduleRepository;

    @Override
    public void syncActiveZeroReportDutyScheduled() {
        log.info("开始执行ZeroReport值班信息定时同步任务 - {}", LocalDateTime.now());
        
        try {
            // 获取所有配置的远程数据库
            List<DatabaseDto> databases = databaseService.queryAll(new DatabaseQueryCriteria());
            if (databases.isEmpty()) {
                log.warn("未配置任何远程数据库，跳过同步任务");
                return;
            }

            int totalProcessed = 0;
            int totalInserted = 0;
            int totalSkipped = 0;
            int successCount = 0;
            int failCount = 0;

            for (DatabaseDto db : databases) {
                try {
                    log.info("开始同步数据库: {} (ID: {})", db.getName(), db.getId());
                    Map<String, Object> result = syncActiveZeroReportDuty(db.getId());
                    
                    if ((Boolean) result.get("success")) {
                        successCount++;
                        int inserted = (Integer) result.get("inserted");
                        int skipped = (Integer) result.get("skipped");
                        int groups = (Integer) result.get("groups");
                        
                        totalInserted += inserted;
                        totalSkipped += skipped;
                        totalProcessed += groups;
                        
                        log.info("数据库 {} 同步完成: 处理 {} 组数据，插入 {} 条，跳过 {} 条", 
                                db.getName(), groups, inserted, skipped);
                    } else {
                        failCount++;
                        log.error("数据库 {} 同步失败: {}", db.getName(), result.get("message"));
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("数据库 {} 同步异常: {}", db.getName(), e.getMessage(), e);
                }
            }

            log.info("ZeroReport值班信息定时同步任务完成 - 总计: 处理 {} 组数据，插入 {} 条，跳过 {} 条，成功 {} 个数据库，失败 {} 个数据库", 
                    totalProcessed, totalInserted, totalSkipped, successCount, failCount);
                    
        } catch (Exception e) {
            log.error("ZeroReport值班信息定时同步任务执行失败: {}", e.getMessage(), e);
        }
    }

    @Override
@Transactional
public Map<String, Object> syncActiveZeroReportDuty(String databaseId) {
    Map<String, Object> result = new HashMap<>();
    try {
        DatabaseDto db = databaseService.findById(databaseId);
        ValidationUtil.isNull(db, "Database", "id", databaseId);

        log.info("开始从数据库 {} 同步ZeroReport值班信息", db.getName());

        try (Connection conn = createRemoteDataSource(db).getConnection()) {
            Map<Long, String> allEvents = new HashMap<>();
            List<DutySchedule> allToSave = new ArrayList<>();
            int totalSkip = 0;
            int round = 1;
            
            // 预先查询部门名称
            Map<Long, String> deptIdToName = new HashMap<>();
            String deptSql = "SELECT id, name FROM sys_dept";
            try (PreparedStatement ps = conn.prepareStatement(deptSql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    deptIdToName.put(rs.getLong("id"), rs.getString("name"));
                }
            }
            log.info("读取到 {} 个部门信息", deptIdToName.size());

            // 记录导入开始时间戳
            LocalDateTime importStartTime = LocalDateTime.now().minusSeconds(2);
            log.info("开始导入，时间戳: {}", importStartTime);
            
            // 多轮导入逻辑 - 改为检查实际成功保存的记录数
            int targetCount = 30;
            int actualSavedCount = 0;
            while (actualSavedCount < targetCount) {
                Map<Long, String> currentEvents = new HashMap<>();
                
                if (round == 1) {
                    // 第一轮：查询 status=1 的事件，限制数量并按创建时间降序排列
                    int remainingCount = targetCount - actualSavedCount;
                    String zeroSql = "SELECT id, subject FROM zeroreport WHERE status = 1 ORDER BY create_time DESC LIMIT " + remainingCount;
                    try (PreparedStatement ps = conn.prepareStatement(zeroSql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Long id = rs.getLong("id");
                            String subject = rs.getString("subject");
                            currentEvents.put(id, subject);
                            allEvents.put(id, subject);
                        }
                    }
                    log.info("第{}轮：查询到 {} 个status=1的ZeroReport事件（限制{}条，按创建时间降序）", round, currentEvents.size(), remainingCount);
                } else {
                    // 后续轮次：查询 status=0 的事件，按事件名称分组，每组选择 create_time 最新的
                    int remainingCount = targetCount - actualSavedCount;
                    int queryLimit = Math.min(50, remainingCount);
                    String zeroSql;
                    if (allEvents.isEmpty()) {
                        zeroSql = "SELECT z1.id, z1.subject FROM zeroreport z1 " +
                                 "INNER JOIN (SELECT subject, MAX(create_time) as max_time FROM zeroreport WHERE status = 0 GROUP BY subject) z2 " +
                                 "ON z1.subject = z2.subject AND z1.create_time = z2.max_time " +
                                 "WHERE z1.status = 0 ORDER BY z1.create_time DESC LIMIT " + queryLimit;
                    } else {
                        zeroSql = "SELECT z1.id, z1.subject FROM zeroreport z1 " +
                                 "INNER JOIN (SELECT subject, MAX(create_time) as max_time FROM zeroreport WHERE status = 0 GROUP BY subject) z2 " +
                                 "ON z1.subject = z2.subject AND z1.create_time = z2.max_time " +
                                 "WHERE z1.status = 0 AND z1.id NOT IN (" + 
                                 allEvents.keySet().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(",")) + 
                                 ") ORDER BY z1.create_time DESC LIMIT " + queryLimit;
                    }
                    
                    try (PreparedStatement ps = conn.prepareStatement(zeroSql);
                         ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Long id = rs.getLong("id");
                            String subject = rs.getString("subject");
                            currentEvents.put(id, subject);
                            allEvents.put(id, subject);
                        }
                    }
                    log.info("第{}轮：查询到 {} 个status=0的ZeroReport事件（按事件名称分组，优先最新时间，限制{}条，剩余{}条）", 
                            round, currentEvents.size(), queryLimit, remainingCount);
                }

                if (currentEvents.isEmpty()) {
                    log.info("第{}轮未找到新的ZeroReport事件，结束导入", round);
                    break;
                }

                List<Long> eventIds = new ArrayList<>(currentEvents.keySet());
                String eventIdPlaceholders = placeholders(eventIds.size());

                // 2. 查询当前轮次的 leader 和 operator 记录，按工作日期排序，并包含备用电话字段
                String leaderSql = "SELECT event_id, work_date, leader_name, leader_phone, leader_call, dept_id FROM arrangement_leader_record WHERE event_id IN (" + eventIdPlaceholders + ") ORDER BY work_date";
                String operSql = "SELECT event_id, work_date, operator_name, operator_phone, operator_call, dept_id FROM arrangement_operator_record WHERE event_id IN (" + eventIdPlaceholders + ") ORDER BY work_date";

                // 临时存储当前轮次的 leader 和 operator 数据
                List<Map<String, Object>> leaderRecords = new ArrayList<>();
                List<Map<String, Object>> operatorRecords = new ArrayList<>();

                try (PreparedStatement ps = conn.prepareStatement(leaderSql)) {
                    setInParams(ps, eventIds);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Map<String, Object> record = new HashMap<>();
                            record.put("event_id", rs.getLong("event_id"));
                            record.put("work_date", rs.getDate("work_date").toLocalDate());
                            record.put("leader_name", rs.getString("leader_name"));
                            
                            // 电话号码fallback逻辑：优先使用leader_phone，为空时使用leader_call，且都要截断
                            final int LEADER_PHONE_MAX_LENGTH = 50;
                            String leaderPhone = rs.getString("leader_phone");
                            if (leaderPhone == null || leaderPhone.trim().isEmpty()) {
                                leaderPhone = rs.getString("leader_call");
                            }
                            if (leaderPhone != null && leaderPhone.length() > LEADER_PHONE_MAX_LENGTH) {
                                log.warn("领导电话号码超长（含fallback），已截断: {}", leaderPhone);
                                leaderPhone = leaderPhone.substring(0, LEADER_PHONE_MAX_LENGTH);
                            }
                            record.put("leader_phone", leaderPhone);
                            
                            record.put("dept_id", rs.getLong("dept_id"));
                            leaderRecords.add(record);
                        }
                    }
                }
                log.info("第{}轮：读取到 {} 条领导值班记录", round, leaderRecords.size());

                try (PreparedStatement ps = conn.prepareStatement(operSql)) {
                    setInParams(ps, eventIds);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Map<String, Object> record = new HashMap<>();
                            record.put("event_id", rs.getLong("event_id"));
                            record.put("work_date", rs.getDate("work_date").toLocalDate());
                            record.put("operator_name", rs.getString("operator_name"));
                            
                            // 电话号码fallback逻辑：优先使用operator_phone，为空时使用operator_call
                            String operatorPhone = rs.getString("operator_phone");
                            if (operatorPhone == null || operatorPhone.trim().isEmpty()) {
                                operatorPhone = rs.getString("operator_call");
                            }
                            record.put("operator_phone", operatorPhone);
                            
                            record.put("dept_id", rs.getLong("dept_id"));
                            operatorRecords.add(record);
                        }
                    }
                }
                log.info("第{}轮：读取到 {} 条操作员值班记录", round, operatorRecords.size());

                // 3. 处理当前轮次的 leader 数据，筛选最新的 work_date
                Map<String, Map<String, Object>> latestLeaders = new HashMap<>();
                for (Map<String, Object> leader : leaderRecords) {
                    String key = leader.get("event_id") + ":" + leader.get("dept_id");
                    if (!latestLeaders.containsKey(key) || 
                        ((LocalDate) leader.get("work_date")).isAfter((LocalDate) latestLeaders.get(key).get("work_date"))) {
                        latestLeaders.put(key, leader);
                    }
                }
                log.info("第{}轮：筛选出 {} 组最新的领导值班信息", round, latestLeaders.size());


                // 5. 组合当前轮次的数据并立即保存，按工作日期排序
                int skip = 0;
                int currentRoundSaved = 0;

                // 将leader记录按工作日期降序排序，确保最新记录优先处理
                List<Map<String, Object>> sortedLeaders = new ArrayList<>(latestLeaders.values());
                sortedLeaders.sort((a, b) -> ((LocalDate) b.get("work_date")).compareTo((LocalDate) a.get("work_date")));
                log.info("第{}轮：记录按工作日期降序排序，最新记录优先处理", round);

                for (Map<String, Object> leader : sortedLeaders) {
                    // 如果已达到目标数量，停止处理
                    if (actualSavedCount >= targetCount) {
                        break;
                    }
                    
                    Long eventId = (Long) leader.get("event_id");
                    Long deptId = (Long) leader.get("dept_id");
                    LocalDate workDate = (LocalDate) leader.get("work_date");

                    List<String> operatorNames = new ArrayList<>();
                    List<String> operatorPhones = new ArrayList<>();

                    for (Map<String, Object> operator : operatorRecords) {
                        if (eventId.equals(operator.get("event_id")) &&
                            deptId.equals(operator.get("dept_id")) &&
                            workDate.equals(operator.get("work_date"))) {
                            operatorNames.add((String) operator.get("operator_name"));
                            operatorPhones.add((String) operator.get("operator_phone"));
                        }
                    }

                    String deptName = deptIdToName.getOrDefault(deptId, String.valueOf(deptId));
                    String eventName = currentEvents.get(eventId);

                    // 去重检查 - 改进的去重逻辑：检查事件名称、部门名称和值班日期的组合
                    if (dutyScheduleRepository.existsByEventNameAndOrgNameAndDutyDate(eventName, deptName, workDate)) {
                        log.debug("跳过重复记录: 事件={}, 部门={}, 日期={}", eventName, deptName, workDate);
                        skip++;
                        continue;
                    }

                    // 创建记录并处理数据截断问题
                    DutySchedule d = new DutySchedule();
                    d.setOrgName(deptName);
                    d.setLeaderName((String) leader.get("leader_name"));
                    
                    // 处理电话号码长度，避免数据截断错误（主号码和 fallback 都要截断）
                    final int LEADER_PHONE_MAX_LENGTH = 50;
                    String leaderPhone = (String) leader.get("leader_phone");
                    if (leaderPhone != null && leaderPhone.length() > LEADER_PHONE_MAX_LENGTH) {
                        log.warn("领导电话号码过长，已截断: {}", leaderPhone);
                        leaderPhone = leaderPhone.substring(0, LEADER_PHONE_MAX_LENGTH);
                    }
                    d.setLeaderPhone(leaderPhone);
                    
                    String dutyPhones = String.join(";", operatorPhones);
                    if (dutyPhones.length() > 255) { // 假设数据库字段长度为255
                        dutyPhones = dutyPhones.substring(0, 255);
                        log.warn("值班电话号码过长，已截断: {}", dutyPhones);
                    }
                    
                    d.setDutyPerson(String.join(";", operatorNames));
                    d.setDutyPhone(dutyPhones);
                    d.setDutyDate(workDate);
                    d.setEventName(eventName);
                    d.setCreatedAt(LocalDateTime.now());
                    d.setUpdatedAt(LocalDateTime.now());
                    
                    // 立即保存记录
                    try {
                        dutyScheduleRepository.save(d);
                        actualSavedCount++;
                        currentRoundSaved++;
                        allToSave.add(d); // 仍然添加到列表中用于最终统计
                        log.debug("成功保存记录: 事件={}, 部门={}, 日期={}, 累计保存={}/{}", 
                                eventName, deptName, workDate, actualSavedCount, targetCount);
                    } catch (Exception e) {
                        log.warn("保存记录失败: 事件={}, 部门={}, 日期={}, 错误: {}", 
                                eventName, deptName, workDate, e.getMessage());
                        skip++;
                    }
                }

                log.info("第{}轮：成功保存 {} 条记录，跳过 {} 条，累计已保存 {} 条", round, currentRoundSaved, skip, actualSavedCount);
                totalSkip += skip;
                
                round++;
                
                // 验证已保存记录数不超过目标数量
                if (actualSavedCount > targetCount) {
                    log.warn("警告: 实际保存记录数({})超过目标数量({})", actualSavedCount, targetCount);
                }
                
                // 如果已达到目标数量，提前结束
                if (actualSavedCount >= targetCount) {
                    log.info("已达到目标记录数量({})，停止导入", targetCount);
                    break;
                }
                
                // 防止无限循环，最多执行10轮
                if (round > 10) {
                    log.warn("已达到最大轮次限制(10轮)，停止导入");
                    break;
                }
            }

            // 清理导入时间戳之前的旧记录
            try {
                int deletedCount = dutyScheduleRepository.deleteByCreatedAtBefore(importStartTime);
                log.info("清理了 {} 条导入时间戳之前的旧记录", deletedCount);
            } catch (Exception e) {
                log.warn("清理旧记录失败: {}", e.getMessage());
            }

            result.put("success", true);
            result.put("inserted", actualSavedCount);
            result.put("skipped", totalSkip);
            result.put("groups", actualSavedCount);
            result.put("rounds", round - 1);

        } catch (Exception ex) {
            log.error("ZeroReport同步失败: {}", ex.getMessage(), ex);
            result.put("success", false);
            result.put("message", ex.getMessage());
        }
        return result;
    } catch (Exception ex) {
        log.error("ZeroReport同步任务前置准备失败: {}", ex.getMessage(), ex);
        result.put("success", false);
        result.put("message", ex.getMessage());
        return result;
    }
}

    private DataSource createRemoteDataSource(DatabaseDto db) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(db.getJdbcUrl());
        ds.setUsername(db.getUserName());
        ds.setPassword(db.getPwd());
        return ds;
    }

    private static String placeholders(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) sb.append(',');
            sb.append('?');
        }
        return sb.toString();
    }

    private static void setInParams(PreparedStatement ps, List<Long> ids) throws SQLException {
        for (int i = 0; i < ids.size(); i++) {
            ps.setLong(i + 1, ids.get(i));
        }
    }


    private static class Key {
        final long deptId;
        final LocalDate workDate;
        final long eventId;
        Key(long deptId, LocalDate workDate, long eventId) { 
            this.deptId = deptId; 
            this.workDate = workDate; 
            this.eventId = eventId;
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return deptId == key.deptId && Objects.equals(workDate, key.workDate) && eventId == key.eventId;
        }
        @Override public int hashCode() { return Objects.hash(deptId, workDate, eventId); }
    }
}
