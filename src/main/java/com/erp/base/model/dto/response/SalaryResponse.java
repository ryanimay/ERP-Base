package com.erp.base.model.dto.response;

import com.erp.base.model.entity.SalaryModel;
import com.erp.base.tool.ObjectTool;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SalaryResponse {
    private Long id;
    private ClientNameObject user;
    private LocalDate time;
    //本薪
    private String baseSalary;
    //餐費
    private String mealAllowance;
    //加給
    private String bonus;
    //勞保
    private String laborInsurance;
    //健保
    private String nationalHealthInsurance;
    //總扣除額
    private String reduceTotal;
    //總額
    private String grandTotal;
    //基底
    private boolean root;

    public SalaryResponse(SalaryModel model) {
        this.id = model.getId();
        this.user = new ClientNameObject(model.getUser());
        this.time = model.getTime();
        this.baseSalary = ObjectTool.formatBigDecimal(model.getBaseSalary());
        this.mealAllowance = ObjectTool.formatBigDecimal(model.getMealAllowance());
        this.bonus = ObjectTool.formatBigDecimal(model.getBonus());
        this.laborInsurance = ObjectTool.formatBigDecimal(model.getLaborInsurance());
        this.nationalHealthInsurance = ObjectTool.formatBigDecimal(model.getNationalHealthInsurance());
        this.reduceTotal = ObjectTool.formatBigDecimal(model.getReduceTotal());
        this.grandTotal = ObjectTool.formatBigDecimal(model.grandTotal());
        this.root = model.isRoot();
    }
}
