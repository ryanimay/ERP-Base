package com.erp.base.model.dto.request.project;

import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.ProjectModel;
import com.erp.base.model.entity.UserModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectRequest implements IBaseDto<ProjectModel> {
    private Long id;
    private String name;
    private String type;//1.開發案 2.維護案
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String info;
    private Long managerId;
    @Override
    public ProjectModel toModel() {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setName(name);
        projectModel.setStartTime(startTime);
        projectModel.setEndTime(endTime);
        projectModel.setInfo(info);
        projectModel.setManager(new UserModel(managerId));
        return null;
    }
}
