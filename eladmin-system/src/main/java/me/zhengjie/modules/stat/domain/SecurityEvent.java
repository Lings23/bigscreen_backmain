package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "security_event")
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_name", nullable = false)
    private String systemName;
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "source", nullable = false)
    private String source;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 