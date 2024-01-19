package com.erp.base.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 採購
 * */
@Entity
@Table(name = "procurement")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcurementModel implements IBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "type")//1.進 2.出
    private int type;
    @Column(name = "name")
    private String name;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "count")
    private long count = 0;
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    @Column(name = "create_by", nullable = false)
    private long createBy;
    @Column(name = "status")
    private int status = 1;//1.pending 2.done
}
