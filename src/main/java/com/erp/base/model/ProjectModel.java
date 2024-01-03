package com.erp.base.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * 專案
 * */
@Entity
@Table(name = "project")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private String type;
    @Column(name = "create_time")
    private LocalDateTime createTime;
    @Column(name = "create_by")
    private long createBy;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "info")
    private String info;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private UserModel Manager;
    @Column(name = "status", nullable = false)
    private String status;
}
