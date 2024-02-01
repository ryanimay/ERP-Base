package com.erp.base.model.dto.response;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.PerformanceModel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PerformanceResponse {
    private long id;
    private String event;
    private ClientNameObject user;
    private BigDecimal fixedBonus;//固定額度績效
    private BigDecimal performanceRatio;//績效比率
    private LocalDateTime eventTime;
    private LocalDateTime createTime;
    private String createBy;
    private String status;

    public PerformanceResponse(PerformanceModel model) {
        this.id = model.getId();
        this.event = model.getEvent();
        ClientModel u = model.getUser();
        this.user = new ClientNameObject(u.getId(), u.getUsername());
        this.fixedBonus = model.getFixedBonus();
        this.performanceRatio = model.getPerformanceRatio();
        this.eventTime = model.getEventTime();
        this.createTime = model.getCreateTime();
        this.createBy = model.getCreateBy().getUsername();
        this.status = StatusConstant.get(model.getStatus());
    }
}
