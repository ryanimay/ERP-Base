package com.erp.base.service;

import com.erp.base.model.constant.RoleConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.department.DepartmentRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.ClientNameRoleObject;
import com.erp.base.model.dto.response.DepartmentResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.DepartmentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private DepartmentService departmentService;

    @Test
    @DisplayName("設定部門預設權限_無部門預設權限_成功")
    void setDepartmentDefaultRole_defaultRole_ok() {
        ClientModel model = new ClientModel(1);
        ClientModel model1 = departmentService.setDepartmentDefaultRole(model, null);
        Assertions.assertTrue(model1.getRoles().contains(new RoleModel(1)));
    }

    @Test
    @DisplayName("設定部門預設權限_成功")
    void setDepartmentDefaultRole_ok() {
        ClientModel model = new ClientModel(1);
        DepartmentModel d = new DepartmentModel();
        d.setDefaultRole(new RoleModel(5));
        Mockito.when(cacheService.getDepartment(Mockito.any())).thenReturn(d);
        ClientModel model1 = departmentService.setDepartmentDefaultRole(model, 1L);
        Assertions.assertTrue(model1.getRoles().contains(new RoleModel(5)));
    }

    @Test
    @DisplayName("找部門ID_null")
    void findById_null() {
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        DepartmentModel byId = departmentService.findById(1L);
        Assertions.assertNull(byId);
    }

    @Test
    @DisplayName("找部門ID_成功")
    void findById_ok() {
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.of(new DepartmentModel(1L)));
        DepartmentModel byId = departmentService.findById(1L);
        Assertions.assertEquals(1L, byId.getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("部門清單_成功")
    void list_ok() {
        List<DepartmentModel> departmentModels = new ArrayList<>();
        DepartmentModel d1 = new DepartmentModel(1L);
        d1.setDefaultRole(new RoleModel(1));
        departmentModels.add(d1);
        DepartmentModel d2 = new DepartmentModel(2L);
        d2.setDefaultRole(new RoleModel(2));
        departmentModels.add(d2);
        DepartmentModel d3 = new DepartmentModel(3L);
        d3.setDefaultRole(new RoleModel(3));
        departmentModels.add(d3);
        Page<DepartmentModel> page = new PageImpl<>(departmentModels);
        Mockito.when(departmentRepository.findAll((Specification<DepartmentModel>)Mockito.any(), (PageRequest)Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> list = departmentService.list(new DepartmentRequest());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, DepartmentResponse.class)), list);
    }

    @Test
    @DisplayName("部門員工清單_未知部門ID_錯誤")
    void findStaffById_unknownDepartmentId_error() {
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = departmentService.findStaffById(1L);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "DepartmentId Not Found."), response);
    }

    @Test
    @DisplayName("部門員工清單_按level排序_成功")
    void findStaffById_ok() {
        DepartmentModel departmentModel = new DepartmentModel(1L);
        List<ClientModel> clientModelList = new ArrayList<>();
        ClientModel c1 = new ClientModel(1);
        clientModelList.add(c1);
        ClientModel c2 = new ClientModel(2);
        clientModelList.add(c2);
        ClientModel c3 = new ClientModel(3);
        HashSet<RoleModel> roles = new HashSet<>();
        RoleModel role = new RoleModel(1L);
        role.setLevel(RoleConstant.LEVEL_3);
        roles.add(role);
        c3.setRoles(roles);
        clientModelList.add(c3);
        departmentModel.setClientModelList(clientModelList);
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.of(departmentModel));
        ResponseEntity<ApiResponse> response = departmentService.findStaffById(1L);
        List<ClientNameRoleObject> expectList = new ArrayList<>();
        expectList.add(new ClientNameRoleObject(c3, RoleConstant.LEVEL_3));
        expectList.add(new ClientNameRoleObject(c1, RoleConstant.LEVEL_0));
        expectList.add(new ClientNameRoleObject(c1, RoleConstant.LEVEL_0));
        Assertions.assertEquals(ApiResponse.success(expectList), response);
    }

    @Test
    @DisplayName("移除部門_未知ID_錯誤")
    void remove_unknownDepartmentId_error() {
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = departmentService.remove(1L);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.DEPARTMENT_IN_USE), response);
    }

    @Test
    @DisplayName("移除部門_使用中不可刪除_錯誤")
    void remove_departmentInUsed_error() {
        DepartmentModel d = new DepartmentModel(1L);
        ArrayList<ClientModel> clientModelList = new ArrayList<>();
        clientModelList.add(new ClientModel(1));
        d.setClientModelList(clientModelList);
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.of(d));
        ResponseEntity<ApiResponse> response = departmentService.remove(1L);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.DEPARTMENT_IN_USE), response);
    }

    @Test
    @DisplayName("部門員工清單_成功")
    void remove_ok() {
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.of(new DepartmentModel(1L)));
        ResponseEntity<ApiResponse> response = departmentService.remove(1L);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }
}