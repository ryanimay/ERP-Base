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
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long createBy;
    private Integer status;

    @Override
    public ProcurementModel toModel() {
        ProcurementModel procurementModel = new ProcurementModel();
        procurementModel.setId(id);
        procurementModel.setType(type);
        procurementModel.setName(name);
        procurementModel.setPrice(price);
        procurementModel.setCount(count);
        procurementModel.setStatus(status);
        return procurementModel;
    }

    @Override
    public Specification<ProcurementModel> getSpecification() {
        GenericSpecifications<ProcurementModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", "=", id)
                .add("type", "=", type)
                .add("name", "like", name)
                .add("createTime", ">=", startTime)
                .add("createTime", "<=", endTime)
                .add("status", "=", status)
                .build();
    }
}
