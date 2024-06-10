package com.erp.base.controller;

import com.erp.base.testConfig.TestUtils;
import com.erp.base.testConfig.redis.TestRedisConfiguration;
import com.erp.base.model.constant.ProcurementConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.procurement.ProcurementRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ProcurementResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.ProcurementModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ProcurementRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootTest(classes = TestRedisConfiguration.class)
@TestPropertySource(locations = {
        "classpath:application-redis-test.properties",
        "classpath:application-quartz-test.properties"
})
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class ProcurementControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private ProcurementRepository procurementRepository;
    private static final String DEFAULT_USERNAME = "test";
    private static final long DEFAULT_UID = 1L;
    private static ClientModel me;

    @BeforeAll
    static void beforeAll(){
        me = new ClientModel(1L);
        me.setUsername(DEFAULT_USERNAME);
        me.setRoles(Set.of(new RoleModel(2L)));
        me.setDepartment(new DepartmentModel(1L));
    }

    @Test
    @DisplayName("採購清單_全搜_成功")
    void procurementList_findAll_ok() throws Exception {
        ProcurementResponse procurement1 = new ProcurementResponse(createProcurement(1, me, 50000, 2, ProcurementConstant.STATUS_PENDING));
        ProcurementResponse procurement2 = new ProcurementResponse(createProcurement(2, me, 30000, 3, ProcurementConstant.STATUS_PENDING));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROCUREMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 2, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(procurement1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(procurement1.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value(procurement1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].price").value(procurement1.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].count").value(procurement1.getCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].total").value(String.valueOf(procurement1.getPrice().multiply(BigDecimal.valueOf(procurement1.getCount())))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(procurement1.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(procurement1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.id").value(procurement1.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.username").value(procurement1.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(procurement2.getStatus()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].id").value(procurement2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].type").value(procurement2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].name").value(procurement2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].price").value(procurement2.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].count").value(procurement2.getCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].total").value(String.valueOf(procurement2.getPrice().multiply(BigDecimal.valueOf(procurement2.getCount())))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].info").value(procurement2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createTime").value(procurement2.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy.id").value(procurement2.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].createBy.username").value(procurement2.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[1].status").value(procurement2.getStatus()));
        procurementRepository.deleteById(procurement1.getId());
        procurementRepository.deleteById(procurement2.getId());
    }

    @Test
    @DisplayName("採購清單_搜TYPE_成功")
    void procurementList_findByType_ok() throws Exception {
        ProcurementResponse procurement1 = new ProcurementResponse(createProcurement(1, me, 50000, 2, ProcurementConstant.STATUS_PENDING));
        ProcurementResponse procurement2 = new ProcurementResponse(createProcurement(2, me, 30000, 3, ProcurementConstant.STATUS_PENDING));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROCUREMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(procurement1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(procurement1.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value(procurement1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].price").value(procurement1.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].count").value(procurement1.getCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].total").value(String.valueOf(procurement1.getPrice().multiply(BigDecimal.valueOf(procurement1.getCount())))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(procurement1.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(procurement1.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.id").value(procurement1.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.username").value(procurement1.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(procurement1.getStatus()));
        procurementRepository.deleteById(procurement1.getId());
        procurementRepository.deleteById(procurement2.getId());
    }

    @Test
    @DisplayName("採購清單_搜Status_成功")
    void procurementList_findByStatus_ok() throws Exception {
        ProcurementResponse procurement1 = new ProcurementResponse(createProcurement(1, me, 50000, 2, ProcurementConstant.STATUS_PENDING));
        ProcurementResponse procurement2 = new ProcurementResponse(createProcurement(2, me, 30000, 3, ProcurementConstant.STATUS_APPROVED));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROCUREMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("status", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        ResultActions resultActions = testUtils.performAndExpectCodeAndMessage(mockMvc, requestBuilder, response);
        testUtils.comparePage(resultActions, 15, 1, 1, 1);
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].id").value(procurement2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].type").value(procurement2.getType()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].name").value(procurement2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].price").value(procurement2.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].count").value(procurement2.getCount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].total").value(String.valueOf(procurement2.getPrice().multiply(BigDecimal.valueOf(procurement2.getCount())))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].info").value(procurement2.getInfo()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createTime").value(procurement2.getCreateTime()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.id").value(procurement2.getCreateBy().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].createBy.username").value(procurement2.getCreateBy().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.data[0].status").value(procurement2.getStatus()));
        procurementRepository.deleteById(procurement1.getId());
        procurementRepository.deleteById(procurement2.getId());
    }

    @Test
    @DisplayName("新增採購_成功")
    void addProcurement_ok() throws Exception {
        ProcurementRequest procurementRequest = new ProcurementRequest();
        procurementRequest.setType(1);
        procurementRequest.setName("test");
        procurementRequest.setPrice(new BigDecimal(500));
        procurementRequest.setCount(5L);
        procurementRequest.setInfo("testInfo");
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(Router.PROCUREMENT.ADD)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(procurementRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        List<ProcurementModel> all = procurementRepository.findAll();
        Optional<ProcurementModel> first = all.stream().filter(p -> p.getName().equals(procurementRequest.getName())).findFirst();
        Assertions.assertTrue(first.isPresent());
        ProcurementModel model = first.get();
        Assertions.assertEquals(procurementRequest.getType(), model.getType());
        Assertions.assertEquals(procurementRequest.getName(), model.getName());
        Assertions.assertEquals(procurementRequest.getPrice(), model.getPrice());
        Assertions.assertEquals(procurementRequest.getCount(), model.getCount());
        Assertions.assertEquals(procurementRequest.getInfo(), model.getInfo());
        Assertions.assertEquals(ProcurementConstant.STATUS_PENDING, model.getStatus());
        procurementRepository.deleteById(model.getId());
    }

    @Test
    @DisplayName("更新採購_成功")
    void updateProcurement_ok() throws Exception {
        ProcurementModel procurement = createProcurement(1, me, 50000, 2, ProcurementConstant.STATUS_PENDING);
        ProcurementRequest procurementRequest = new ProcurementRequest();
        procurementRequest.setId(procurement.getId());
        procurementRequest.setPrice(new BigDecimal(100000));
        procurementRequest.setCount(3L);
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PROCUREMENT.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(procurementRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        List<ProcurementModel> all = procurementRepository.findAll();
        Optional<ProcurementModel> first = all.stream().filter(p -> p.getId() == procurementRequest.getId()).findFirst();
        Assertions.assertTrue(first.isPresent());
        ProcurementModel model = first.get();
        Assertions.assertEquals(procurement.getType(), model.getType());
        Assertions.assertEquals(procurement.getName(), model.getName());
        Assertions.assertEquals(procurementRequest.getPrice(), model.getPrice());
        Assertions.assertEquals(procurementRequest.getCount(), model.getCount());
        Assertions.assertEquals(procurement.getInfo(), model.getInfo());
        Assertions.assertEquals(ProcurementConstant.STATUS_PENDING, model.getStatus());
        procurementRepository.deleteById(model.getId());
    }

    @Test
    @DisplayName("更新採購_未知Id_錯誤")
    void updateProcurement_unknownId_error() throws Exception {
        ProcurementRequest procurementRequest = new ProcurementRequest();
        procurementRequest.setId(99L);
        ResponseEntity<ApiResponse> response = ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(Router.PROCUREMENT.UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectTool.toJson(procurementRequest))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
    }

    @Test
    @DisplayName("移除採購_成功")
    void deleteProcurement_ok() throws Exception {
        ProcurementModel procurement = createProcurement(1, me, 50000, 2, ProcurementConstant.STATUS_PENDING);
        Optional<ProcurementModel> byId = procurementRepository.findById(procurement.getId());
        Assertions.assertTrue(byId.isPresent());
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(Router.PROCUREMENT.DELETE)
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", String.valueOf(procurement.getId()))
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_UID));
        testUtils.performAndExpect(mockMvc, requestBuilder, response);
        byId = procurementRepository.findById(procurement.getId());
        Assertions.assertTrue(byId.isEmpty());
    }

    private ProcurementModel createProcurement(int type, ClientModel model, int price, int count, int status){
        ProcurementModel procurementModel = new ProcurementModel();
        procurementModel.setType(type);
        procurementModel.setName("採購:" + model.getUsername());
        procurementModel.setPrice(new BigDecimal(price));
        procurementModel.setCount(count);
        procurementModel.setInfo("詳細內容" + model.getUsername());
        procurementModel.setCreateBy(model);
        procurementModel.setStatus(status);
        return procurementRepository.save(procurementModel);
    }
}