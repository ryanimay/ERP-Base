package com.erp.base.model.dto.request.job;

import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.JobModel;
import com.erp.base.model.entity.UserModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
public class JobRequest implements IBaseDto<JobModel> {
    private Long id;
    private String info;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public JobModel toModel() {
        JobModel job = new JobModel();
        job.setId(id);
        job.setInfo(info);
        if(userId != null) job.setUser(new UserModel(userId));
        job.setStartTime(startTime);
        job.setEndTime(endTime);
        return job;
    }

    @Override
    public Specification<JobModel> getSpecification() {
        return null;
    }
}
