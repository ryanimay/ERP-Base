package com.erp.base.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * 出缺勤
 */
@Entity
@Table(name = "attend")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;
    @Column(name = "attend_time")
    private LocalDateTime attendTime = LocalDateTime.now();
    @Column(name = "leave_time")
    private LocalDateTime leaveTime;
    @Column(name = "remarks")
    private String remarks;
}
