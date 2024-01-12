package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系統通知
 */
@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "to")
    private long to;
    @Column(name = "info")
    private String info;
    @ManyToOne
    @JoinColumn(name="router_id")
    private RouterModel router;
    @Column(name = "status")
    private boolean status = true;
    @Column(name = "global")
    private boolean global = false;
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    @Column(name = "create_By")
    private long createBy = 0;

    public String getRouterName() {
        return router.getName();
    }

    public void setRouterName(String name) {
        if(router == null) router = new RouterModel();
        this.router.setName(name);
    }
}
