package com.erp.base.model.dto.request.performance;

import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.PerformanceModel;
import com.erp.base.tool.DateTool;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "績效請求")
public class PerformanceRequest extends PageRequestParam implements IBaseDto<PerformanceModel> {
    @Schema(description = "績效ID")
    private Long id;
    @Schema(description = "事件")
    private String event;
    @Schema(description = "申請人ID")
    private Long userId;
    @Schema(description = "固定加給")
    private BigDecimal fixedBonus;
    @Schema(description = "比例加給")
    private BigDecimal performanceRatio;
    @Schema(description = "事件時間")
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime eventTime;
    @Schema(description = "狀態")
    private Integer status;
    @Schema(description = "開始時間")
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime startTime;
    @Schema(description = "結束時間")
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime endTime;

    @Override
    public PerformanceModel toModel() {
        PerformanceModel performanceModel = new PerformanceModel();
        performanceModel.setEvent(event);
        performanceModel.setFixedBonus(fixedBonus);
        performanceModel.setPerformanceRatio(performanceRatio);
        performanceModel.setStatus(status == null || StatusConstant.get(status) == null ? StatusConstant.PENDING_NO : status);
        performanceModel.setEventTime(eventTime);
        return performanceModel;
    }

    @Override
    @JsonIgnore
    public Specification<PerformanceModel> getSpecification() {
        GenericSpecifications<PerformanceModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications.add("id", GenericSpecifications.EQ, id)
                .add("user", GenericSpecifications.EQ, userId == null ? null : new ClientModel(userId))
                .add("createTime", GenericSpecifications.GOE, startTime)
                .add("createTime", GenericSpecifications.LOE, endTime)
                .add("status", GenericSpecifications.EQ, status == null ? null : StatusConstant.get(status) == null ? StatusConstant.PENDING_NO : status)
                .buildAnd();
    }
}
