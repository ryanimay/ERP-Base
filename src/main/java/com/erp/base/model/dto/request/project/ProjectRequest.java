package com.erp.base.model.dto.request.project;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.ProjectModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "專案共用請求")
public class ProjectRequest implements IBaseDto<ProjectModel> {
    @Schema(description = "專案ID")
    private Long id;
    @Schema(description = "專案名稱")
    private String name;
    @Schema(description = "專案類別")
    private String type;//1.開發案 2.維護案 3.其他
    @Schema(description = "計畫開始時間")
    private LocalDateTime scheduledStartTime;
    @Schema(description = "計畫結束時間")
    private LocalDateTime scheduledEndTime;
    @Schema(description = "內容")
    private String info;
    @Schema(description = "管理人ID")
    private Long managerId;
    @Schema(description = "狀態")
    private Integer status;
    @Schema(description = "標記顏色")
    private String markColor;

    @Override
    public ProjectModel toModel() {
        ProjectModel projectModel = new ProjectModel();
        projectModel.setName(name);
        projectModel.setType(type);
        projectModel.setScheduledStartTime(scheduledStartTime);
        projectModel.setScheduledEndTime(scheduledEndTime);
        projectModel.setInfo(info);
        projectModel.setManager(new ClientModel(managerId));
        projectModel.setMarkColor(markColor);
        return projectModel;
    }

    @Override
    @JsonIgnore
    public Specification<ProjectModel> getSpecification() {
        GenericSpecifications<ProjectModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", GenericSpecifications.EQ, id)
                .add("name", GenericSpecifications.LIKE, name)
                .add("type", GenericSpecifications.EQ, type)
                .add("scheduledStartTime", GenericSpecifications.GOE, scheduledStartTime)
                .add("scheduledEndTime", GenericSpecifications.LOE, scheduledEndTime)
                .add("manager", GenericSpecifications.EQ, managerId == null ? null : new ClientModel(managerId))
                .add("status", GenericSpecifications.EQ, status)
                .buildAnd();
    }
}
