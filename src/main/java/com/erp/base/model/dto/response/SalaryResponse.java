package com.erp.base.model.dto.response;

import com.erp.base.model.entity.SalaryModel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalaryResponse {
    private Long id;
    private ClientNameObject user;
    private LocalDate time;
    //本薪
    private BigDecimal baseSalary;
    //餐費
    private BigDecimal mealAllowance;
    //加給
    private BigDecimal bonus;
    //勞保
    private BigDecimal laborInsurance;
    //健保
    private BigDecimal nationalHealthInsurance;
    //總扣除額
    private BigDecimal reduceTotal;
    //總額
    private BigDecimal grandTotal;
    //基底
    private boolean root;

    public SalaryResponse(SalaryModel model) {
        this.id = model.getId();
        this.user = new ClientNameObject(model.getUser());
        this.time = model.getTime();
        this.baseSalary = model.getBaseSalary();
        this.mealAllowance = model.getMealAllowance();
        this.bonus = model.getBonus();
        this.laborInsurance = model.getLaborInsurance();
        this.nationalHealthInsurance = model.getNationalHealthInsurance();
        this.reduceTotal = model.getReduceTotal();
        this.grandTotal = model.grandTotal();
        this.root = model.isRoot();
    }
}
