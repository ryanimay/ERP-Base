package com.erp.base.model.dto.request.project;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.ProjectModel;
import com.erp.base.model.entity.ClientModel;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
public class ProjectRequest implements IBaseDto<ProjectModel> {
    private Long id;
    private String name;
    private String type;//1.開發案 2.維護案
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    private String info;
    private Long managerId;

    @Override
    public ProjectModel toModel() {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setName(name);
        projectModel.setType(type);
        projectModel.setScheduledStartTime(scheduledStartTime);
        projectModel.setScheduledEndTime(scheduledEndTime);
        projectModel.setInfo(info);
        projectModel.setManager(new ClientModel(managerId));
        return projectModel;
    }

    @Override
    public Specification<ProjectModel> getSpecification() {
        GenericSpecifications<ProjectModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", GenericSpecifications.EQ, id)
                .add("name", GenericSpecifications.LIKE, name)
                .add("type", GenericSpecifications.EQ, type)
                .build();
    }
}
