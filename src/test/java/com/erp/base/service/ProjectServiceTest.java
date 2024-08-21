package com.erp.base.service;


import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.OrderRequest;
import com.erp.base.model.dto.request.project.ProjectRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ProjectResponse;
import com.erp.base.model.dto.security.ClientIdentityDto;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.ProjectModel;
import com.erp.base.repository.ProjectRepository;
import com.erp.base.service.security.UserDetailImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectService projectService;

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("專案清單_成功")
    void list_ok() {
        List<ProjectModel> projectModels = new ArrayList<>();
        ProjectModel projectModel = new ProjectModel();
        projectModel.setId(1L);
        ClientModel createBy = new ClientModel(1);
        createBy.setUsername("test");
        projectModel.setCreateBy(createBy);
        projectModel.setManager(createBy);
        projectModel.setInfo("test");
        projectModels.add(projectModel);
        Mockito.when(projectRepository.findAll((Specification<ProjectModel>)Mockito.any(), (Sort)Mockito.any())).thenReturn(projectModels);
        ResponseEntity<ApiResponse> all = projectService.list(new ProjectRequest());
        Assertions.assertEquals(ApiResponse.success(projectModels.stream().map(ProjectResponse::new).toList()), all);
    }

    @Test
    @DisplayName("新增專案_成功")
    void add_ok() {
        ClientModel clientModel = new ClientModel(1);
        UserDetailImpl principal = new UserDetailImpl(new ClientIdentityDto(clientModel), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ProjectRequest request = new ProjectRequest();
        request.setId(1L);
        request.setManagerId(1L);
        ResponseEntity<ApiResponse> all = projectService.add(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), all);
    }

    @Test
    @DisplayName("更新專案_未知ID_錯誤")
    void update_unknownId_error() {
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        ProjectRequest request = new ProjectRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> all = projectService.update(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR), all);
    }

    @Test
    @DisplayName("更新專案_成功")
    void update_ok() {
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new ProjectModel()));
        ProjectRequest request = new ProjectRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> all = projectService.update(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), all);
    }

    @Test
    @DisplayName("專案啟動_未知ID_錯誤")
    void start_error() {
        Mockito.when(projectRepository.start(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(0);
        ResponseEntity<ApiResponse> start = projectService.start(1L);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UPDATE FAILED, ID[" + 1 + "]"), start);
    }

    @Test
    @DisplayName("專案啟動_成功")
    void start_ok() {
        Mockito.when(projectRepository.start(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(1);
        ResponseEntity<ApiResponse> start = projectService.start(1L);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), start);
    }

    @Test
    @DisplayName("專案結案_未知ID_錯誤")
    void done_error() {
        Mockito.when(projectRepository.done(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(0);
        ResponseEntity<ApiResponse> done = projectService.done(1L);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "UPDATE FAILED, ID[" + 1 + "]"), done);
    }

    @Test
    @DisplayName("專案結案_成功")
    void done_ok() {
        Mockito.when(projectRepository.done(Mockito.anyLong(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(1);
        ResponseEntity<ApiResponse> done = projectService.done(1L);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), done);
    }

    @Test
    @DisplayName("專案排序_參數為空_成功")
    void order_emptyParam_ok() {
        ResponseEntity<ApiResponse> done = projectService.order(new ArrayList<>());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), done);
    }

    @Test
    @DisplayName("專案排序_成功")
    void order_ok() {
        ArrayList<OrderRequest> orders = new ArrayList<>();
        orders.add(new OrderRequest("1", 2));
        orders.add(new OrderRequest("2", 3));
        ResponseEntity<ApiResponse> done = projectService.order(orders);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), done);
    }
}