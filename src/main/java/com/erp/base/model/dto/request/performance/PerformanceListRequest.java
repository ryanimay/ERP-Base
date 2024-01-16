package com.erp.base.model.dto.request.performance;

import com.erp.base.model.dto.request.PageRequestParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class PerformanceListRequest extends PageRequestParam {
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long status;
}
