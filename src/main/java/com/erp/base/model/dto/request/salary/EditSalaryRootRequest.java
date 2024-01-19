package com.erp.base.model.dto.request.salary;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.entity.SalaryModel;
import com.erp.base.model.entity.ClientModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditSalaryRootRequest implements IBaseDto<SalaryModel> {
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
    private boolean root = true;

    @Override
    public SalaryModel toModel() {
        SalaryModel salaryModel = new SalaryModel();
        salaryModel.setId(id);
        salaryModel.setUser(new ClientModel(userId));
        salaryModel.setBaseSalary(baseSalary);
        salaryModel.setBonus(bonus);
        salaryModel.setLaborInsurance(laborInsurance);
        salaryModel.setNationalHealthInsurance(nationalHealthInsurance);
        return salaryModel;
    }

    @Override
    public Specification<SalaryModel> getSpecification() {
        GenericSpecifications<SalaryModel> genericSpecifications = new GenericSpecifications<>();
        return genericSpecifications
                .add("id", "=", id)
                .build();
    }
}
