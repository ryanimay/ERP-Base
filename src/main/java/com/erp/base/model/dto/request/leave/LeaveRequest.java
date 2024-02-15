package com.erp.base.model.dto.request.leave;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.LeaveModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
@Schema(description = "假單請求")
public class LeaveRequest implements IBaseDto<LeaveModel> {
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
    public Specification<LeaveModel> getSpecification() {
        return null;
    }
}
