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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

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
    @Schema(description = "請假人ID")
    private Long userId;
    @Schema(description = "搜尋用時間")
    @DateTimeFormat(pattern = DateTool.YYYY_MM)
    private LocalDate searchTime;

    @Override
    public LeaveModel toModel() {
        LeaveModel leaveModel = new LeaveModel();
        if(type != null) leaveModel.setType(type);
        if(startTime != null) leaveModel.setStartTime(startTime);
        if(endTime != null) leaveModel.setEndTime(endTime);
        leaveModel.setStatus(StatusConstant.PENDING_NO);//初始化都是待審
        if(info != null) leaveModel.setInfo(info);
        if(userId != null) leaveModel.setUser(new ClientModel(userId));
        return leaveModel;
    }

    @Override
    @JsonIgnore
    public Specification<LeaveModel> getSpecification() {
        GenericSpecifications<LeaveModel> genericSpecifications = new GenericSpecifications<>();
        Specification<LeaveModel> specification1 = genericSpecifications
                .add("user", GenericSpecifications.EQ, userId == null ? null : new ClientModel(userId))
                .buildAnd();

        genericSpecifications = new GenericSpecifications<>();
        LocalDateTime currentMonthStart = searchTime == null ? null : searchTime.withDayOfMonth(1).atStartOfDay();
        LocalDateTime currentMonthEnd = searchTime == null ? null : searchTime.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);
        Specification<LeaveModel> specification2 = genericSpecifications
                //開始時間介於搜尋月分中
                .addBetween("startTime", GenericSpecifications.BETWEEN, new LocalDateTime[]{currentMonthStart, currentMonthEnd})
                //結束時間介於搜尋月分中
                .addBetween("endTime", GenericSpecifications.BETWEEN, new LocalDateTime[]{currentMonthStart, currentMonthEnd})
                .buildOr();

        genericSpecifications = new GenericSpecifications<>();
        Specification<LeaveModel> specification3 = genericSpecifications
                //開始時間少於搜尋月
                .add("startTime", GenericSpecifications.LOE, currentMonthEnd)
                //結束時間大於搜尋月
                .add("endTime", GenericSpecifications.GOE, currentMonthStart)
                .buildAnd();
        //WHERE userId = ? AND ( startTime BETWEEN ? and ? OR endTime BETWEEN ? and ? OR ( startTime <= ? AND endTime >= ?))
        return specification1.and(specification2.or(specification3));
    }
}
