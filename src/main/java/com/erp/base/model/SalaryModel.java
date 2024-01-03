package com.erp.base.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
/**
 * 薪資
 */
@Entity
@Table(name = "salary")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;
    @Column(name = "time", nullable = false)
    private String time;
    //本薪
    @Column(name = "base_salary", precision = 10, scale = 2)
    private BigDecimal baseSalary = BigDecimal.valueOf(0);
    //餐費
    @Column(name = "meal_allowance", precision = 10, scale = 2)
    private BigDecimal mealAllowance = BigDecimal.valueOf(0);
    //加給
    @Column(name = "bonus", precision = 10, scale = 2)
    private BigDecimal bonus = BigDecimal.valueOf(0);
    //勞保
    @Column(name = "labor_insurance", precision = 10, scale = 2)
    private BigDecimal laborInsurance = BigDecimal.valueOf(0);
    //健保
    @Column(name = "national_health_insurance", precision = 10, scale = 2)
    private BigDecimal nationalHealthInsurance = BigDecimal.valueOf(0);

    public YearMonth getYearMonth() {
        return YearMonth.parse(time);
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.time = yearMonth.toString();
    }
    //總扣除額
    public BigDecimal getReduceTotal(){
        return laborInsurance.add(nationalHealthInsurance);
    }
    //總額
    public BigDecimal grandTotal(){
        return baseSalary.add(mealAllowance).add(bonus).subtract(getReduceTotal());
    }
}
