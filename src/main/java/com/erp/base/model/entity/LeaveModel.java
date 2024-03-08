package com.erp.base.model.entity;

import com.erp.base.model.constant.StatusConstant;
import com.erp.base.tool.DateTool;
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private ClientModel user;//請假人
    @Column(name = "type", nullable = false)
    private int type; //請假類型
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startTime;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "status", nullable = false)
    //只會有1.待審 2.已審未執行 3.已執行
    private int status = StatusConstant.PENDING_NO;
    @Column(name = "info")
    private String info;
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime = DateTool.now();

}
