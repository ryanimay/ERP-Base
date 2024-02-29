package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.SalaryResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.entity.SalaryModel;
import com.erp.base.repository.SalaryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@DirtiesContext
class SalaryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private SalaryRepository salaryRepository;
    private static final String DEFAULT_USER_NAME = "test";
    private static ClientModel me;

    @BeforeAll
    static void beforeAll(){
        me = new ClientModel(1L);
        me.setUsername(DEFAULT_USER_NAME);
        me.setRoles(Set.of(new RoleModel(2L)));
        me.setDepartment(new DepartmentModel(1L));
    }

    @Test
    @DisplayName("薪資設定清單_成功")
    void salaryRootList_ok() throws Exception {
        SalaryResponse salaryResponse1 = new SalaryResponse(createSalary(true));
        SalaryModel salary2 = createSalary(false);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.SALARY.ROOTS)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(salaryResponse1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.id").value(salaryResponse1.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].user.username").value(salaryResponse1.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].time").value(salaryResponse1.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].baseSalary").value(salaryResponse1.getBaseSalary()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].mealAllowance").value(salaryResponse1.getMealAllowance()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].bonus").value(salaryResponse1.getBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].laborInsurance").value(salaryResponse1.getLaborInsurance()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].nationalHealthInsurance").value(salaryResponse1.getNationalHealthInsurance()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].reduceTotal").value(salaryResponse1.getReduceTotal()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].grandTotal").value(salaryResponse1.getGrandTotal()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].root").value(salaryResponse1.isRoot()));
        salaryRepository.deleteById(salaryResponse1.getId());
        salaryRepository.deleteById(salary2.getId());
    }

    private SalaryModel createSalary(boolean root){
        SalaryModel salaryModel = new SalaryModel();
        salaryModel.setUser(me);
        salaryModel.setBaseSalary(new BigDecimal(50000));
        salaryModel.setMealAllowance(new BigDecimal(2400));
        salaryModel.setBonus(new BigDecimal(1000));
        salaryModel.setLaborInsurance(new BigDecimal(1000));
        salaryModel.setNationalHealthInsurance(new BigDecimal(1000));
        salaryModel.setRoot(root);
        return salaryRepository.save(salaryModel);
    }
}