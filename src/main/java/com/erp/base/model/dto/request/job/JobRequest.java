package com.erp.base.model.dto.request.job;

import com.erp.base.enums.StatusConstant;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.JobModel;
import com.erp.base.tool.DateTool;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class JobRequest implements IBaseDto<JobModel> {
    private Long id;
    private String info;
    private Long userId;
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime startTime;
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime endTime;
    private int status;

    @Override
    public JobModel toModel() {
        JobModel job = new JobModel();
        if(id != null) job.setId(id);
        job.setInfo(info);
        if(userId != null) job.setUser(new ClientModel(userId));
        job.setStartTime(startTime);
        job.setEndTime(endTime);
        return job;
    }

    @Override
    public Specification<JobModel> getSpecification() {
        return null;
    }

    public String getStatus() {
        return StatusConstant.get(status);
    }
}
