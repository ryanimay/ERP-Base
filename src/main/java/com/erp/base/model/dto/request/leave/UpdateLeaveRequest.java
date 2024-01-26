package com.erp.base.model.dto.request.leave;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.LeaveModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class UpdateLeaveRequest extends AddLeaveRequest implements IBaseDto<LeaveModel> {
    private long id;
    private LocalDateTime createdTime;

    @Override
    public LeaveModel toModel() {
        LeaveModel leaveModel = new LeaveModel();
        leaveModel.setId(id);
        leaveModel.setType(getType());
        leaveModel.setStartTime(getStartTime());
        leaveModel.setEndTime(getEndTime());
        leaveModel.setCreatedTime(createdTime);
        leaveModel.setStatus(StatusConstant.get(1));
        leaveModel.setInfo(getInfo());
        return leaveModel;
    }
}
