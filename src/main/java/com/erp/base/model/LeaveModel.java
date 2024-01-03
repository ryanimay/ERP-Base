package com.erp.base.model;

import com.erp.base.enums.JobStatusEnum;
import com.erp.base.enums.LeaveEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * 請假
 */
@Entity
@Table(name = "leave")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;
    @Column(name = "type", nullable = false)
    private String type; //請假類型
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "info")
    private String info;
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

}
