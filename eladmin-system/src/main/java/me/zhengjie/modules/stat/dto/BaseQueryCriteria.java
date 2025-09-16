package me.zhengjie.modules.stat.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 基础查询条件类
 * @author system
 * @date 2025-09-08
 */
@Data
public class BaseQueryCriteria {
    
    /**
     * 开始日期
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    
    /**
     * 关键字搜索
     */
    private String keyword;
    
    /**
     * 状态筛选
     */
    private String status;
    
    /**
     * 创建时间开始
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAtStart;
    
    /**
     * 创建时间结束
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAtEnd;
    
    /**
     * 更新时间开始
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAtStart;
    
    /**
     * 更新时间结束
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAtEnd;
}
