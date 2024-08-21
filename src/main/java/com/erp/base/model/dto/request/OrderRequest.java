package com.erp.base.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "排序請求物件")
public class OrderRequest {
    private String id;
    private Integer order;
}
