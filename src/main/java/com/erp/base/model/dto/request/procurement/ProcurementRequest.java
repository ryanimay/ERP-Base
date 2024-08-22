package com.erp.base.model.dto.request.procurement;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.constant.ProcurementConstant;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.ProcurementModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "採購共用請求")
public class ProcurementRequest extends PageRequestParam implements IBaseDto<ProcurementModel> {
    @Schema(description = "採購單ID")
    private Long id;
    @Schema(description = "類型")
    private Integer type;
    @Schema(description = "品名")
    private String name;
    @Schema(description = "單價")
    private BigDecimal price;
    @Schema(description = "數量")
    private Long count;
    @Schema(description = "描述")
    private String info;
    @Schema(description = "開始時間")
    private LocalDateTime startTime;
    @Schema(description = "結束時間")
    private LocalDateTime endTime;
    @Schema(description = "狀態")
    private Integer status;

    @Override
    public ProcurementModel toModel() {
        ProcurementModel procurementModel = new ProcurementModel();
        procurementModel.setType(type);
        procurementModel.setName(name);
        procurementModel.setPrice(price);
        procurementModel.setCount(count);
        procurementModel.setInfo(info);
        return procurementModel;
    }

    @Override
    @JsonIgnore
    public Specification<ProcurementModel> getSpecification() {
        GenericSpecifications<ProcurementModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", GenericSpecifications.EQ, id)
                .add("type", GenericSpecifications.EQ, type)
                .add("name", GenericSpecifications.LIKE, name)
                .add("createTime", GenericSpecifications.GOE, startTime)
                .add("createTime", GenericSpecifications.LOE, endTime)
                .add("status", GenericSpecifications.EQ, status == null || ProcurementConstant.get(status) == null ? null : status)
                .buildAnd();
    }
}
