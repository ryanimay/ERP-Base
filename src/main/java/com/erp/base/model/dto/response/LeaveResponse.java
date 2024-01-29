package com.erp.base.model.dto.response;

import com.erp.base.enums.LeaveConstant;
import com.erp.base.enums.StatusConstant;
import com.erp.base.model.entity.LeaveModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LeaveResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -3L;

    private long id;
    private String username;
    private String type; //請假類型
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String info;
    private LocalDateTime createdTime;

    public LeaveResponse(LeaveModel model) {
        this.id = model.getId();
        this.username = model.getUser().getUsername();
        this.type = LeaveConstant.get(model.getType());
        this.startTime = model.getStartTime();
        this.endTime = model.getEndTime();
        this.status = StatusConstant.get(model.getStatus());
        this.info = model.getInfo();
        this.createdTime = model.getCreatedTime();
    }
}
