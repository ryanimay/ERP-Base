package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 任務
 * */
@Entity
@Table(name = "quartz_job")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuartzJobModel implements IBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "cron")
    private String cron;
    @Column(name = "param")
    private String param;
    @Column(name = "info")
    private String info;
    @Column(name = "class_path")
    private String classPath;
    @Column(name = "status")
    private boolean status = true;
}

