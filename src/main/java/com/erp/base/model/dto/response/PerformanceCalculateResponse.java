package com.erp.base.model.dto.response;

import com.erp.base.tool.ObjectTool;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class PerformanceCalculateResponse {
    private ClientNameObject user;
    private BigDecimal fixedBonus;
    private BigDecimal performanceRatio;
    private String settleYear;
    private Long count;

    public String getFixedBonus() {
        return ObjectTool.formatBigDecimal(fixedBonus);
    }
    public String getPerformanceRatio() {
        return ObjectTool.formatBigDecimal(performanceRatio);
    }
}
