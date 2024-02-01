package com.erp.base.model.dto.request.performance;

import lombok.Data;

@Data
public class PerformanceAcceptRequest {
    private Long eventId;
    private Long eventUserId;
}
