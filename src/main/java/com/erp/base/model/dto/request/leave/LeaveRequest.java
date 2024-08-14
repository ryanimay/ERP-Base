package com.erp.base.model.dto.request.leave;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.LeaveModel;
import com.erp.base.tool.DateTool;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "假單請求")
public class LeaveRequest extends PageRequestParam implements IBaseDto<LeaveModel> {
    @Schema(description = "假單ID")
    private long id;
    @Schema(description = "請假類型")
    private Integer type;
    @Schema(description = "開始時間")
    private LocalDateTime startTime;
    @Schema(description = "結束時間")
    private LocalDateTime endTime;
    @Schema(description = "附註")
    private String info;
    @Schema(description = "搜尋用UserId")
    private Long userId;
    @Schema(description = "搜尋用時間")
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime searchTime;

    @Override
    public LeaveModel toModel() {
        LeaveModel leaveModel = new LeaveModel();
        if(type != null) leaveModel.setType(type);
        if(startTime != null) leaveModel.setStartTime(startTime);
        if(endTime != null) leaveModel.setEndTime(endTime);
        leaveModel.setStatus(StatusConstant.PENDING_NO);//初始化都是待審
        if(info != null) leaveModel.setInfo(info);
        return leaveModel;
    }

    @Override
    @JsonIgnore
    public Specification<LeaveModel> getSpecification() {
        GenericSpecifications<LeaveModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("user", GenericSpecifications.EQ, userId == null ? null : new ClientModel(userId))
                .add("startTime", GenericSpecifications.LOE, searchTime)
                .add("endTime", GenericSpecifications.GOE, searchTime)
                .build();
    }
}
