package com.erp.base.model.dto.request.log;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.LogModel;
import com.erp.base.tool.DateTool;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class LogRequest extends PageRequestParam implements IBaseDto<LogModel> {
    private Long id;
    private Boolean status;
    private String user;
    private String url;
    private String ip;
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime startTime;
    @DateTimeFormat(pattern = DateTool.YYYY_MM_DD_T_HH_MM_SS)
    private LocalDateTime endTime;

    @Override
    public LogModel toModel() {
        return null;
    }

    @Override
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
