package com.erp.base.model.entity;

import com.erp.base.tool.DateTool;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 系統通知
 */
@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationModel implements IBaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "info")
    private String info;
    @Column(name="router")
    private String router;
    @Column(name = "status")//已處理
    @Builder.Default
    private boolean status = true;
    @Column(name = "global")
    @Builder.Default
    private boolean global = false;
    @Column(name = "create_time")
    @Builder.Default
    private LocalDateTime createTime = DateTool.now();
    @Column(name = "create_by")
    @Builder.Default
    private long createBy = 0;
    @ManyToMany(mappedBy = "notifications", fetch = FetchType.LAZY)
    private Set<ClientModel> clients;
}
