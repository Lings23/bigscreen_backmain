package me.zhengjie.modules.stat.domain;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attack_stats")
public class AttackStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "malicious_code_attack", nullable = false)
    private Integer maliciousCodeAttack; // 恶意代码攻击数量

    @Column(name = "vulnerability_attack", nullable = false)
    private Integer vulnerabilityAttack; // 漏洞攻击数量

    @Column(name = "dos_attack", nullable = false)
    private Integer dosAttack; // 拒绝服务攻击数量（DoS）

    @Column(name = "scan_probe", nullable = false)
    private Integer scanProbe; // 扫描探测数量

    @Column(name = "other_attack", nullable = false)
    private Integer otherAttack; // 其他类型攻击数量

    @Column(name = "stat_date", nullable = false)
    private LocalDateTime statDate; // 统计日期

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间
}