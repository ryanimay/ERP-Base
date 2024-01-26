package com.erp.base.model.entity;

import com.erp.base.enums.StatusConstant;
import com.erp.base.tool.DateTool;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * 任務
 * */
@Entity
@Table(name = "job")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobModel implements IBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "info")
    private String info;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private ClientModel user;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime = DateTool.now();
    @Column(name = "create_by", nullable = false)
    private long createBy;
    @Column(name = "status", nullable = false)
    private String status = StatusConstant.get(StatusConstant.PENDING_NO);
}
