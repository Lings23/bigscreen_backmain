package me.zhengjie.modules.stat.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 安全事件查询条件
 * @author system
 * @date 2025-09-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityEventQueryCriteria extends BaseQueryCriteria {
    
    /**
     * 系统名称
     */
    private String systemName;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 事件时间开始
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime eventTimeStart;
    
    /**
     * 事件时间结束
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime eventTimeEnd;
    
    /**
     * 事件来源
     */
    private String source;
    
    /**
     * 内容关键字搜索
     */
    private String contentKeyword;
}
