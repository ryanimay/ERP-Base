package com.erp.base.model.dto.request.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "審核假單請求")
public class LeaveAcceptRequest {
    @Schema(description = "假單ID")
    private Long id;
    @Schema(description = "假單申請人ID")
    private Long eventUserId;
}
