package com.erp.base.model.dto.request.leave;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.LeaveModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
public class LeaveRequest implements IBaseDto<LeaveModel> {
    private long id;
    private Integer type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
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
