package com.erp.base.controller;

import com.erp.base.config.TestUtils;
import com.erp.base.config.redis.TestRedisConfiguration;
import com.erp.base.enums.ProcurementConstant;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ProcurementResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.ProcurementModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.ProcurementRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @PersistenceContext
    private EntityManager entityManager;
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
    @DisplayName("採購清單_全搜_成功")
    @WithUserDetails(DEFAULT_USER_NAME)
    void procurementList_findAll_ok() throws Exception {
        ProcurementResponse procurement1 = new ProcurementResponse(createProcurement(1, me, 50000, 2, ProcurementConstant.STATUS_PENDING));
        ProcurementResponse procurement2 = new ProcurementResponse(createProcurement(2, me, 30000, 3, ProcurementConstant.STATUS_PENDING));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS, true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROCUREMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
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
    @WithUserDetails(DEFAULT_USER_NAME)
    void procurementList_findByType_ok() throws Exception {
        ProcurementResponse procurement1 = new ProcurementResponse(createProcurement(1, me, 50000, 2, ProcurementConstant.STATUS_PENDING));
        ProcurementResponse procurement2 = new ProcurementResponse(createProcurement(2, me, 30000, 3, ProcurementConstant.STATUS_PENDING));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS, true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROCUREMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("type", "1")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
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
    @WithUserDetails(DEFAULT_USER_NAME)
    void procurementList_findByStatus_ok() throws Exception {
        ProcurementResponse procurement1 = new ProcurementResponse(createProcurement(1, me, 50000, 2, ProcurementConstant.STATUS_PENDING));
        ProcurementResponse procurement2 = new ProcurementResponse(createProcurement(2, me, 30000, 3, ProcurementConstant.STATUS_APPROVED));
        ResponseEntity<ApiResponse> response = ApiResponse.success(ApiResponseCode.SUCCESS, true);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(Router.PROCUREMENT.LIST)
                .contentType(MediaType.APPLICATION_JSON)
                .param("status", "2")
                .header(HttpHeaders.AUTHORIZATION, testUtils.createTestToken(DEFAULT_USER_NAME));
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