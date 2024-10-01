package com.erp.base.model.dto.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PerformanceCountDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 5L;
    private String pendingCount;
    private String approvedCount;

    public PerformanceCountDto(Long pendingCount, Long approvedCount) {
        this.pendingCount = pendingCount == null ? "0" : String.valueOf(pendingCount);
        this.approvedCount = approvedCount == null ? "0" : String.valueOf(approvedCount);
    }
}
