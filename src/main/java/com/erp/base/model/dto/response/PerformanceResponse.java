package com.erp.base.model.dto.response;

import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.PerformanceModel;
import com.erp.base.tool.DateTool;
import com.erp.base.tool.ObjectTool;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerformanceResponse {
    private long id;
    private String event;
    private ClientNameObject user;
    private BigDecimal fixedBonus;//固定額度績效
    private BigDecimal performanceRatio;//績效比率
    private String eventTime;
    private String createTime;
    private String createBy;
    private String status;

    public PerformanceResponse(PerformanceModel model) {
        this.id = model.getId();
        this.event = model.getEvent();
        ClientModel u = model.getUser();
        this.user = new ClientNameObject(u.getId(), u.getUsername());
        this.fixedBonus = model.getFixedBonus();
        this.performanceRatio = model.getPerformanceRatio();
        this.eventTime = DateTool.format(model.getEventTime());
        this.createTime = DateTool.format(model.getCreateTime());
        this.createBy = model.getCreateBy().getUsername();
        this.status = StatusConstant.get(model.getStatus());
    }

    public String getFixedBonus() {
        return ObjectTool.formatBigDecimal(fixedBonus);
    }
    public String getPerformanceRatio() {
        return ObjectTool.formatBigDecimal(performanceRatio);
    }
}
