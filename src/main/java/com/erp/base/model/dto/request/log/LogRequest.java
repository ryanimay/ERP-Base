package com.erp.base.model.dto.request.log;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.LogModel;
import com.erp.base.tool.DateTool;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "日誌共用請求")
public class LogRequest extends PageRequestParam implements IBaseDto<LogModel> {
    @Schema(description = "日誌ID")
    private Long id;
    @Schema(description = "日誌狀態")
    private Boolean status;
    @Schema(description = "操作人")
    private String user;
    @Schema(description = "請求路徑")
    private String url;
    @Schema(description = "操作IP")
    private String ip;
    @Schema(description = "開始時間")
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime startTime;
    @Schema(description = "結束時間")
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime endTime;

    @Override
    public LogModel toModel() {
        return null;
    }

    @Override
    @JsonIgnore
    public Specification<LogModel> getSpecification() {
        GenericSpecifications<LogModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", GenericSpecifications.EQ, id)
                .add("userName", GenericSpecifications.LIKE, user)
                .add("status", GenericSpecifications.EQ, status)
                .add("ip", GenericSpecifications.EQ, ip)
                .add("time", GenericSpecifications.GOE, startTime)
                .add("time", GenericSpecifications.LOE, endTime)
                .build();
    }
}
