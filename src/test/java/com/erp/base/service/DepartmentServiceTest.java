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
        Iterator<RoleModel> iterator = model1.getRoles().iterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(1L, iterator.next().getId());
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    @DisplayName("設定部門預設權限_成功")
    void setDepartmentDefaultRole_ok() {
        ClientModel model = new ClientModel(1);
        DepartmentModel d = new DepartmentModel();
        d.setDefaultRole(new RoleModel(5));
        Mockito.when(cacheService.getDepartment(Mockito.any())).thenReturn(d);
        ClientModel model1 = departmentService.setDepartmentDefaultRole(model, 1L);
        Iterator<RoleModel> iterator = model1.getRoles().iterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(5L, iterator.next().getId());
        Assertions.assertFalse(iterator.hasNext());
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
    @DisplayName("部門員工清單_成功")
    void findStaffById_ok() {
        DepartmentModel departmentModel = new DepartmentModel(1L);
        List<ClientModel> clientModelList = new ArrayList<>();
        ClientModel c1 = new ClientModel(1);
        HashSet<RoleModel> roles1 = new HashSet<>();
        RoleModel role1 = new RoleModel(1L);
        role1.setLevel(RoleConstant.LEVEL_0);
        roles1.add(role1);
        c1.setRoles(roles1);
        clientModelList.add(c1);
        ClientModel c2 = new ClientModel(2);
        HashSet<RoleModel> roles2 = new HashSet<>();
        RoleModel role2 = new RoleModel(2L);
        role2.setLevel(RoleConstant.LEVEL_1);
        roles2.add(role2);
        c2.setRoles(roles2);
        clientModelList.add(c2);
        ClientModel c3 = new ClientModel(3);
        HashSet<RoleModel> roles3 = new HashSet<>();
        RoleModel role3 = new RoleModel(3L);
        role3.setLevel(RoleConstant.LEVEL_3);
        roles3.add(role1);
        roles3.add(role2);
        roles3.add(role3);
        c3.setRoles(roles3);
        clientModelList.add(c3);
        ClientModel c4 = new ClientModel(3);
        HashSet<RoleModel> roles4 = new HashSet<>();
        roles4.add(role3);
        roles4.add(role2);
        roles4.add(role1);
        c4.setRoles(roles4);
        departmentModel.setClientModelList(clientModelList);
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.of(departmentModel));
        ResponseEntity<ApiResponse> response = departmentService.findStaffById(1L);
        List<ClientNameRoleObject> expectList = new ArrayList<>();
        expectList.add(new ClientNameRoleObject(c1));
        expectList.add(new ClientNameRoleObject(c2));
        expectList.add(new ClientNameRoleObject(c4));
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
    @DisplayName("部門角色關聯解除_成功")
    void remove_ok() {
        Mockito.when(departmentRepository.findById(Mockito.any())).thenReturn(Optional.of(new DepartmentModel(1L)));
        ResponseEntity<ApiResponse> response = departmentService.remove(1L);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), response);
    }

    @Test
    @DisplayName("總部門數量_成功")
    void getSystemDepartment_ok() {
        Mockito.when(departmentRepository.count()).thenReturn(3L);
        String num = departmentService.getSystemDepartment();
        Assertions.assertEquals("3", num);
    }
}