package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "duty_schedule")
public class DutySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_name", nullable = false)
    private String orgName;
    @Column(name = "leader_name", nullable = false)
    private String leaderName;
    @Column(name = "leader_phone", nullable = false)
    private String leaderPhone;
    @Column(name = "duty_person", nullable = false, length = 1024)
    private String dutyPerson;
    @Column(name = "duty_phone", nullable = false, length = 1024)
    private String dutyPhone;
    @Column(name = "duty_date", nullable = false)
    private LocalDate dutyDate;
    @Column(name = "event_name")
    private String eventName;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 