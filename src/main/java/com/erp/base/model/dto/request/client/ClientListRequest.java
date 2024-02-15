package com.erp.base.model.dto.request.client;

import com.erp.base.model.dto.request.PageRequestParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用戶清單請求")
public class ClientListRequest extends PageRequestParam {
    @Schema(description = "搜尋類型1.id 2.name")
    private int type;
    @Schema(description = "用戶ID")
    private Long id;
    @Schema(description = "用戶名")
    private String name;
}
