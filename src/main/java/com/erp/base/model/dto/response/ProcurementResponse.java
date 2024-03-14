package com.erp.base.model.dto.response;

import com.erp.base.model.constant.ProcurementConstant;
import com.erp.base.model.entity.ProcurementModel;
import com.erp.base.tool.DateTool;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProcurementResponse {
    private long id;
    private int type;
    private String name;
    private BigDecimal price;
    private long count;
    private BigDecimal total;
    private String info;
    private String createTime;
    private ClientNameObject createBy;
    private String status;

    public ProcurementResponse(ProcurementModel model) {
        this.id = model.getId();
        this.type = model.getType();
        this.name = model.getName();
        this.price = model.getPrice();
        this.count = model.getCount();
        this.info = model.getInfo();
        this.createTime = DateTool.format(model.getCreateTime());
        this.createBy = new ClientNameObject(model.getCreateBy());
        this.status = ProcurementConstant.get(model.getStatus());
        this.total = price == null ? BigDecimal.valueOf(0) : price.multiply(BigDecimal.valueOf(count));
    }
}
