package com.erp.base.model.dto.response;

import com.erp.base.model.constant.LeaveConstant;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.LeaveModel;
import com.erp.base.tool.DateTool;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LeaveResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -3L;

    private long id;
    private ClientNameObject user;
    private LeaveTypeResponse type; //請假類型
    private String startTime;
    private String endTime;
    private String status;
    private String info;
    private String createdTime;

    public LeaveResponse(LeaveModel model) {
        this.id = model.getId();
        ClientModel u = model.getUser();
        this.user = new ClientNameObject(u.getId(), u.getUsername());
        this.type = new LeaveTypeResponse(model.getType(), LeaveConstant.get(model.getType()));
        this.startTime = DateTool.format(model.getStartTime());
        this.endTime = DateTool.format(model.getEndTime());
        this.status = StatusConstant.get(model.getStatus());
        this.info = model.getInfo();
        this.createdTime = DateTool.format(model.getCreatedTime());
    }
}
