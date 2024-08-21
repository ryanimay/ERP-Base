package com.erp.base.model.entity;

import com.erp.base.tool.DateTool;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 日誌
 */
@Entity
@Table(name = "log")
@Getter
@Setter
public class LogModel implements IBaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "status")
    private Boolean status;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "url")
    private String url;
    @Column(name = "params", length = 1000)
    private String params;
    @Column(name = "result")
    private String result;
    @Column(name = "ip")
    private String ip;
    @Column(name = "time")
    private LocalDateTime time;

    public LogModel() {
        this.time = DateTool.now();
        this.status = false;
    }
}
