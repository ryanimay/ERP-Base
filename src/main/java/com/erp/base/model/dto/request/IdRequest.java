package com.erp.base.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ID請求封裝類")
public class IdRequest {
    private Long id;
}
