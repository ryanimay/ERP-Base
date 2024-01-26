package com.erp.base.model.dto.response;

import com.erp.base.model.entity.JobModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobResponse {
    private long id;
    private String info;
    private String username;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdTime;
    private long createBy;
    private String status;

    public JobResponse(JobModel model) {
        this.id = model.getId();
        this.info = model.getInfo();
        this.username = model.getUser().getUsername();
        this.startTime = model.getStartTime();
        this.endTime = model.getEndTime();
        this.createdTime = model.getCreatedTime();
        this.createBy = model.getCreateBy();
        this.status = model.getStatus();
    }
}
