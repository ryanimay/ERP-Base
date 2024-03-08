package com.erp.base.model.entity;

import com.erp.base.model.constant.ProcurementConstant;
import com.erp.base.tool.DateTool;
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
    @Column(name = "info")
    private String info;
    @Column(name = "create_time")
    private LocalDateTime createTime = DateTool.now();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "create_by", nullable = false)
    private ClientModel createBy;
    @Column(name = "status")
    private int status = ProcurementConstant.STATUS_PENDING;
}
