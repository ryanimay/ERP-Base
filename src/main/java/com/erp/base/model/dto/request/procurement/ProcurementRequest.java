package com.erp.base.model.dto.request.procurement;

import com.erp.base.model.dto.request.PageRequestParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ProcurementRequest extends PageRequestParam {
    private long id;
    private int type;
    private String name;
    private BigDecimal price;
    private long count;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long createBy;
}
