package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private ClientModel user;
    @Column(name = "date")
    private LocalDate date = LocalDate.now();
    @Column(name = "attend_time")
    private LocalDateTime attendTime;
    @Column(name = "leave_time")
    private LocalDateTime leaveTime;
    @Column(name = "remarks")
    private String remarks;

    public AttendModel(ClientModel user) {
        this.user = user;
    }
}
