package com.erp.base.model.dto.response;

import com.erp.base.enums.ProcurementConstant;
import com.erp.base.model.entity.ProcurementModel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ProcurementResponse {
    private long id;
    private int type;
    private String name;
    private BigDecimal price;
    private long count;
    private BigDecimal total;
    private String info;
    private LocalDateTime createTime;
    private ClientNameObject createBy;
    private String status;

    public ProcurementResponse(ProcurementModel model) {
        this.id = model.getId();
        this.type = model.getType();
        this.name = model.getName();
        this.price = model.getPrice();
        this.count = model.getCount();
        this.info = model.getInfo();
        this.createTime = model.getCreateTime();
        this.createBy = new ClientNameObject(model.getCreateBy());
        this.status = ProcurementConstant.get(model.getStatus());
        this.total = price.multiply(BigDecimal.valueOf(count));
    }
}
