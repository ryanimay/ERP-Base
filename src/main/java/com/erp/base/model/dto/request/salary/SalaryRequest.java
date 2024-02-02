package com.erp.base.model.dto.request.salary;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.SalaryModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
public class SalaryRequest extends PageRequestParam implements IBaseDto<SalaryModel> {
    private Long id;
    private Long userId;
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
    //基底
    private Boolean root;

    @Override
    public SalaryModel toModel() {
        SalaryModel salaryModel = new SalaryModel();
        salaryModel.setId(id);
        salaryModel.setUser(new ClientModel(userId));
        salaryModel.setBaseSalary(baseSalary);
        salaryModel.setMealAllowance(mealAllowance);
        salaryModel.setBonus(bonus);
        salaryModel.setLaborInsurance(laborInsurance);
        salaryModel.setNationalHealthInsurance(nationalHealthInsurance);
        if(root != null) salaryModel.setRoot(root);
        return salaryModel;
    }
    //只用來搜root
    @Override
    public Specification<SalaryModel> getSpecification() {
        GenericSpecifications<SalaryModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", GenericSpecifications.EQ, id)
                .add("user", GenericSpecifications.EQ, userId == null ? null : new ClientModel(userId))
                .add("root", GenericSpecifications.EQ, true)
                .build();
    }
}
