package com.erp.base.model.dto.request.performance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "審核績效請求")
public class PerformanceAcceptRequest {
    @Schema(description = "績效ID")
    private Long eventId;
    @Schema(description = "績效申請人ID")
    private Long eventUserId;
}
