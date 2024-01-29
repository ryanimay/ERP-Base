package com.erp.base.model.entity;

import com.erp.base.tool.DateTool;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * 績效
 */
@Entity
@Table(name = "performance")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceModel implements IBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "event", nullable = false)
    private String event;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private ClientModel user;
    @Column(name = "fixed_bonus", precision = 10, scale = 2)
    private BigDecimal fixedBonus;
    @Column(name = "performance_ratio", precision = 3, scale = 1)
    private BigDecimal performanceRatio;
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;
    @Column(name = "create_time")
    private LocalDateTime createTime = DateTool.now();
    @Column(name = "create_by")
    private Long createBy;
    @Column(name = "status")//1.待審 2.已審 3.已結 4.移除
    private Long status;
}
