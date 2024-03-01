package com.erp.base.controller;

import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.request.salary.SalaryRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.SalaryResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.model.entity.SalaryModel;
import com.erp.base.repository.SalaryRepository;
import com.erp.base.tool.ObjectTool;
import org.junit.jupiter.api.Assertions;
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

    @Test
    @DisplayName("新增薪資設定_未填用戶_錯誤")
    void editRoot_noUser_error() throws Exception {
        SalaryRequest request = new SalaryRequest();
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.SALARY.EDIT_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("新增薪資設定_不存在用戶_錯誤")
    void editRoot_userNotFound_error() throws Exception {
        SalaryRequest request = new SalaryRequest();
        request.setUserId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.USER_NOT_FOUND);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.SALARY.EDIT_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("新增薪資設定_成功")
    void editRoot_addSalaryRoot_ok() throws Exception {
        SalaryModel byUserIdAndRoot = salaryRepository.findByUserIdAndRoot(me.getId(), true);
        Assertions.assertNull(byUserIdAndRoot);
        SalaryRequest request = new SalaryRequest();
        request.setUserId(me.getId());
        request.setBaseSalary(new BigDecimal(50000));
        request.setMealAllowance(new BigDecimal(2400));
        request.setBonus(new BigDecimal(1000));
        request.setLaborInsurance(new BigDecimal(1000));
        request.setNationalHealthInsurance(new BigDecimal(1000));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.SALARY.EDIT_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        byUserIdAndRoot = salaryRepository.findByUserIdAndRoot(me.getId(), true);
        Assertions.assertNotNull(byUserIdAndRoot);
        Assertions.assertEquals(request.getUserId(), byUserIdAndRoot.getUser().getId());
        Assertions.assertEquals(ObjectTool.formatBigDecimal(request.getBaseSalary()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getBaseSalary()));
        Assertions.assertEquals(ObjectTool.formatBigDecimal(request.getMealAllowance()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getMealAllowance()));
        Assertions.assertEquals(ObjectTool.formatBigDecimal(request.getBonus()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getBonus()));
        Assertions.assertEquals(ObjectTool.formatBigDecimal(request.getLaborInsurance()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getLaborInsurance()));
        Assertions.assertEquals(ObjectTool.formatBigDecimal(request.getNationalHealthInsurance()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getNationalHealthInsurance()));
        salaryRepository.deleteById(byUserIdAndRoot.getId());
    }

    @Test
    @DisplayName("編輯薪資設定_成功")
    void editRoot_editExistsSalaryRoot_ok() throws Exception {
        SalaryModel salary = createSalary(true);
        SalaryRequest request = new SalaryRequest();
        request.setUserId(salary.getUser().getId());
        request.setBaseSalary(new BigDecimal(70000));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.SALARY.EDIT_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(request))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        SalaryModel byUserIdAndRoot = salaryRepository.findByUserIdAndRoot(salary.getUser().getId(), true);
        Assertions.assertNotNull(byUserIdAndRoot);
        Assertions.assertEquals(ObjectTool.formatBigDecimal(request.getBaseSalary()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getBaseSalary()));
        Assertions.assertEquals(ObjectTool.formatBigDecimal(salary.getMealAllowance()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getMealAllowance()));
        Assertions.assertEquals(ObjectTool.formatBigDecimal(salary.getBonus()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getBonus()));
        Assertions.assertEquals(ObjectTool.formatBigDecimal(salary.getLaborInsurance()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getLaborInsurance()));
        Assertions.assertEquals(ObjectTool.formatBigDecimal(salary.getNationalHealthInsurance()), ObjectTool.formatBigDecimal(byUserIdAndRoot.getNationalHealthInsurance()));
        salaryRepository.deleteById(salary.getId());
    }

    @Test
    @DisplayName("個人薪資單列表_成功")
    void getSalaries_ok() throws Exception {
        SalaryModel salary0 = createSalary(true);
        SalaryModel salary1 = createSalary(false);
        SalaryModel salary2 = createSalary(false);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.SALARY.GET)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(salary1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].user.id").value(salary1.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].user.username").value(salary1.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].time").value(salary1.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].baseSalary").value(ObjectTool.formatBigDecimal(salary1.getBaseSalary())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].mealAllowance").value(ObjectTool.formatBigDecimal(salary1.getMealAllowance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].bonus").value(ObjectTool.formatBigDecimal(salary1.getBonus())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].laborInsurance").value(ObjectTool.formatBigDecimal(salary1.getLaborInsurance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].nationalHealthInsurance").value(ObjectTool.formatBigDecimal(salary1.getNationalHealthInsurance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].reduceTotal").value(ObjectTool.formatBigDecimal(salary1.getReduceTotal())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].grandTotal").value(
                        ObjectTool.formatBigDecimal(salary1.getBaseSalary()
                                .add(salary1.getMealAllowance())
                                .add(salary1.getBonus())
                                .subtract(salary1.getReduceTotal()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].root").value(salary1.isRoot()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(salary2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].user.id").value(salary2.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].user.username").value(salary2.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].time").value(salary2.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].baseSalary").value(ObjectTool.formatBigDecimal(salary2.getBaseSalary())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].mealAllowance").value(ObjectTool.formatBigDecimal(salary2.getMealAllowance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].bonus").value(ObjectTool.formatBigDecimal(salary2.getBonus())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].laborInsurance").value(ObjectTool.formatBigDecimal(salary2.getLaborInsurance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].nationalHealthInsurance").value(ObjectTool.formatBigDecimal(salary2.getNationalHealthInsurance())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].reduceTotal").value(ObjectTool.formatBigDecimal(salary2.getReduceTotal())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].grandTotal").value(
                        ObjectTool.formatBigDecimal(salary2.getBaseSalary()
                                .add(salary2.getMealAllowance())
                                .add(salary2.getBonus())
                                .subtract(salary2.getReduceTotal()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].root").value(salary2.isRoot()));
        salaryRepository.deleteById(salary0.getId());
        salaryRepository.deleteById(salary1.getId());
        salaryRepository.deleteById(salary2.getId());
    }

    @Test
    @DisplayName("薪資明細_未知Id_錯誤")
    void salaryInfo_unknownId_error() throws Exception {
        String id = "99";
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id[" + id + "] Not Found");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.SALARY.INFO)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", id)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("薪資明細_未填Id_錯誤")
    void salaryInfo_noId_error() throws Exception {
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id[" + null + "] Not Found");
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.SALARY.INFO)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("薪資明細_成功")
    void salaryInfo_ok() throws Exception {
        SalaryResponse salaryResponse = new SalaryResponse(createSalary(false));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS, salaryResponse);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.SALARY.INFO)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(salaryResponse.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(salaryResponse.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.id").value(salaryResponse.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.user.username").value(salaryResponse.getUser().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.time").value(salaryResponse.getTime().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.baseSalary").value(salaryResponse.getBaseSalary()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.mealAllowance").value(salaryResponse.getMealAllowance()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.bonus").value(salaryResponse.getBonus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.laborInsurance").value(salaryResponse.getLaborInsurance()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nationalHealthInsurance").value(salaryResponse.getNationalHealthInsurance()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reduceTotal").value(salaryResponse.getReduceTotal()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.grandTotal").value(salaryResponse.getGrandTotal()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.root").value(salaryResponse.isRoot()));
        salaryRepository.deleteById(salaryResponse.getId());
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