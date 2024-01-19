package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * 請假
 */
@Entity
@Table(name = "leave")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveModel implements IBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ClientModel user;
    @Column(name = "type", nullable = false)
    private String type; //請假類型
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endTime;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "info")
    private String info;
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

}
