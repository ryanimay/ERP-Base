package com.erp.base.model.dto.request.performance;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.PerformanceModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class PerformanceRequest extends PageRequestParam implements IBaseDto<PerformanceModel> {
    private Long id;
    private String event;
    private Long userId;
    private BigDecimal fixedBonus;
    private BigDecimal performanceRatio;
    private LocalDateTime eventTime;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public PerformanceModel toModel() {
        PerformanceModel performanceModel = new PerformanceModel();
        performanceModel.setEvent(event);
        performanceModel.setFixedBonus(fixedBonus);
        performanceModel.setPerformanceRatio(performanceRatio);
        performanceModel.setStatus(status == null ? StatusConstant.PENDING_NO : status);
        performanceModel.setEventTime(eventTime);
        return performanceModel;
    }

    @Override
    public Specification<PerformanceModel> getSpecification() {
        GenericSpecifications<PerformanceModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications.add("id", GenericSpecifications.EQ, id)
                .add("user", GenericSpecifications.EQ, userId == null ? null : new ClientModel(userId))
                .add("createTime", GenericSpecifications.GOE, startTime)
                .add("createTime", GenericSpecifications.LOE, endTime)
                .add("status", GenericSpecifications.EQ, status)
                .build();
    }
}
