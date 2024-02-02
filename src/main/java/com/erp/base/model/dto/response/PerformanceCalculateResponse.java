package com.erp.base.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PerformanceCalculateResponse {
    private ClientNameObject user;
    private BigDecimal fixedBonus;
    private BigDecimal performanceRatio;
    private String settleYear;
    private Long count;
}
