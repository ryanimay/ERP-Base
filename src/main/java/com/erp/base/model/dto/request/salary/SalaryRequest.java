package com.erp.base.model.dto.request.salary;

import com.erp.base.model.GenericSpecifications;
import com.erp.base.model.dto.request.IBaseDto;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.SalaryModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "薪資單共用請求")
public class SalaryRequest extends PageRequestParam implements IBaseDto<SalaryModel> {
    @Schema(description = "薪資單ID")
    private Long id;
    @Schema(description = "用戶ID")
    private Long userId;
    @Schema(description = "本薪")
    private BigDecimal baseSalary;
    @Schema(description = "餐費")
    private BigDecimal mealAllowance;
    @Schema(description = "加給")
    private BigDecimal bonus;
    @Schema(description = "勞保")
    private BigDecimal laborInsurance;
    @Schema(description = "健保")
    private BigDecimal nationalHealthInsurance;
    @Schema(description = "是否為薪資設定")
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
