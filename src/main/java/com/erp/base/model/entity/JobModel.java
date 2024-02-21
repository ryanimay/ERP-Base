package com.erp.base.model.entity;

import com.erp.base.enums.StatusConstant;
import com.erp.base.tool.DateTool;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private ClientModel user;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime = DateTool.now();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "createBy", nullable = false)
    private ClientModel createBy;
    //只會有1.待執行 2.執行中 3.已完成
    @Column(name = "status", nullable = false)
    private Integer status = StatusConstant.PENDING_NO;
    @Column(name = "order_num")
    private Integer order;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tracking_job",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<ClientModel> trackingList = new HashSet<>();

    public void addTracking(ClientModel model){
        trackingList.add(model);
    }
}
