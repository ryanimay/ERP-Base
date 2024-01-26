package com.erp.base.model.dto.request.leave;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.LeaveModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
public class AddLeaveRequest implements IBaseDto<LeaveModel> {
    private String type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String info;

    @Override
    public LeaveModel toModel() {
        LeaveModel leaveModel = new LeaveModel();
        leaveModel.setType(type);
        leaveModel.setStartTime(startTime);
        leaveModel.setEndTime(endTime);
        leaveModel.setStatus(StatusConstant.get(1));
        leaveModel.setInfo(info);
        return leaveModel;
    }

    @Override
    public Specification<LeaveModel> getSpecification() {
        return null;
    }
}
