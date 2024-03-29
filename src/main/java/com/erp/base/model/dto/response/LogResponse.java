package com.erp.base.model.dto.response;

import com.erp.base.model.entity.LogModel;
import com.erp.base.tool.DateTool;
import lombok.Data;

@Data
public class LogResponse {
    private Long id;
    private Boolean status;
    private String user;
    private String url;
    private String ip;
    private String params;
    private String time;
    private String result;

    public LogResponse(LogModel model) {
        this.id = model.getId();
        this.status = model.getStatus();
        this.user = model.getUserName();
        this.url = model.getUrl();
        this.params = model.getParams();
        this.ip = model.getIp();
        this.time = DateTool.format(model.getTime());
        this.result = model.getResult();
    }
}
