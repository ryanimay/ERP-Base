package com.erp.base.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @Column(name = "info")
    private String info;
    @ManyToOne
    @JoinColumn(name="router_id")
    private RouterModel router;
    @Column(name = "status")
    private boolean status = true;
}
