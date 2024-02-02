package com.erp.base.model.dto.request.procurement;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.ProcurementModel;
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
public class ProcurementRequest extends PageRequestParam implements IBaseDto<ProcurementModel> {
    private Long id;
    private Integer type;
    private String name;
    private BigDecimal price;
    private Long count;
    private String info;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
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
    public Specification<ProcurementModel> getSpecification() {
        GenericSpecifications<ProcurementModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", GenericSpecifications.EQ, id)
                .add("type", GenericSpecifications.EQ, type)
                .add("name", GenericSpecifications.LIKE, name)
                .add("createTime", GenericSpecifications.GOE, startTime)
                .add("createTime", GenericSpecifications.LOE, endTime)
                .add("status", GenericSpecifications.EQ, status)
                .build();
    }
}
