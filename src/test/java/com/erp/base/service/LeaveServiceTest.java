package com.erp.base.service;


import com.erp.base.model.constant.RoleConstant;
import com.erp.base.model.constant.StatusConstant;
import com.erp.base.model.constant.response.ApiResponseCode;
import com.erp.base.model.dto.request.PageRequestParam;
import com.erp.base.model.dto.request.leave.LeaveAcceptRequest;
import com.erp.base.model.dto.request.leave.LeaveRequest;
import com.erp.base.model.dto.response.ApiResponse;
import com.erp.base.model.dto.response.LeaveResponse;
import com.erp.base.model.dto.response.PageResponse;
import com.erp.base.model.entity.ClientModel;
import com.erp.base.model.entity.DepartmentModel;
import com.erp.base.model.entity.LeaveModel;
import com.erp.base.model.entity.RoleModel;
import com.erp.base.repository.LeaveRepository;
import com.erp.base.service.security.UserDetailImpl;
import com.erp.base.tool.BeanProviderTool;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {
    @Mock
    private LeaveRepository leaveRepository;
    private static final MessageSource messageSource = Mockito.spy(MessageSource.class);
    @InjectMocks
    private LeaveService leaveService;

    @BeforeAll
    static void beforeAll() {
        Mockito.mockStatic(BeanProviderTool.class).when(() -> BeanProviderTool.getBean(MessageSource.class)).thenReturn(messageSource);
    }

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        leaveService.setMessageService(Mockito.mock(MessageService.class));
        leaveService.setNotificationService(Mockito.mock(NotificationService.class));
        leaveService.setClientService(Mockito.mock(ClientService.class));
    }

    @Test
    @DisplayName("假單_找不到用戶_錯誤")
    void list_userNotFound_error() {
        ResponseEntity<ApiResponse> list = leaveService.list(new PageRequestParam());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), list);
    }

    @Test
    @DisplayName("假單_成功")
    void list_ok() {
        UserDetailImpl principal = new UserDetailImpl(new ClientModel(1), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        List<LeaveModel> leaveModels = new ArrayList<>();
        LeaveModel leaveModel = new LeaveModel();
        leaveModel.setId(1);
        leaveModel.setInfo("test");
        leaveModel.setUser(new ClientModel(1));
        leaveModel.setType(1);
        leaveModels.add(leaveModel);
        Page<LeaveModel> page = new PageImpl<>(leaveModels);
        Mockito.when(leaveRepository.findAllByUser(Mockito.anyLong(), Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> list = leaveService.list(new PageRequestParam());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, LeaveResponse.class)), list);
    }

    @Test
    @DisplayName("新增假單_找不到用戶_錯誤")
    void add_userNotFound_error() {
        ResponseEntity<ApiResponse> add = leaveService.add(new LeaveRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), add);
    }

    @Test
    @DisplayName("新增假單_成功")
    void add_ok() {
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LeaveModel lm = new LeaveModel();
        lm.setId(1);
        lm.setType(1);
        lm.setUser(clientModel);
        lm.setInfo("test");
        Mockito.when(leaveRepository.save(Mockito.any())).thenReturn(lm);
        ResponseEntity<ApiResponse> add = leaveService.add(new LeaveRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, new LeaveResponse(lm)), add);
    }

    @Test
    @DisplayName("更新假單_找不到用戶_錯誤")
    void update_userNotFound_error() {
        ResponseEntity<ApiResponse> add = leaveService.update(new LeaveRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), add);
    }

    @Test
    @DisplayName("更新假單_未知ID_錯誤")
    void update_unknownId_error() {
        Mockito.when(leaveRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<ApiResponse> add = leaveService.update(new LeaveRequest());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found"), add);
    }

    @Test
    @DisplayName("更新假單_成功")
    void update_ok() {
        LeaveModel lm = new LeaveModel();
        lm.setId(1);
        lm.setInfo("test");
        lm.setType(1);
        lm.setUser(new ClientModel(1));
        Mockito.when(leaveRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(lm));
        Mockito.when(leaveRepository.save(Mockito.any())).thenReturn(lm);
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResponseEntity<ApiResponse> add = leaveService.update(new LeaveRequest());
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS, new LeaveResponse(lm)), add);
    }

    @Test
    @DisplayName("刪除假單_未知ID/錯誤")
    void delete_unknownError() {
        Mockito.when(leaveRepository.deleteByIdAndStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(2);
        ResponseEntity<ApiResponse> delete = leaveService.delete(1);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found"), delete);
    }

    @Test
    @DisplayName("刪除假單_成功")
    void delete_ok() {
        Mockito.when(leaveRepository.deleteByIdAndStatus(Mockito.anyLong(), Mockito.anyInt())).thenReturn(1);
        ResponseEntity<ApiResponse> delete = leaveService.delete(1);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), delete);
    }

    @Test
    @DisplayName("審核假單_未知ID/錯誤")
    void accept_unknownError() {
        Mockito.when(leaveRepository.updateLeaveStatus(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(2);
        LeaveAcceptRequest request = new LeaveAcceptRequest();
        request.setId(1L);
        ResponseEntity<ApiResponse> delete = leaveService.accept(request);
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.UNKNOWN_ERROR, "Id Not Found"), delete);
    }

    @Test
    @DisplayName("審核假單_成功")
    void accept_ok() {
        UserDetailImpl principal = new UserDetailImpl(new ClientModel(1), null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(leaveRepository.updateLeaveStatus(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(1);
        LeaveAcceptRequest request = new LeaveAcceptRequest();
        request.setId(1L);
        request.setEventUserId(1L);
        ResponseEntity<ApiResponse> delete = leaveService.accept(request);
        Assertions.assertEquals(ApiResponse.success(ApiResponseCode.SUCCESS), delete);
    }

    @Test
    @DisplayName("待審核假單_找不到用戶_錯誤")
    void pendingList_userNotFound_error() {
        ResponseEntity<ApiResponse> delete = leaveService.pendingList(new PageRequestParam());
        Assertions.assertEquals(ApiResponse.error(ApiResponseCode.USER_NOT_FOUND), delete);
    }

    @Test
    @DisplayName("待審核假單_管理層全搜_成功")
    void pendingList_isManager_ok() {
        ClientModel clientModel = new ClientModel(1);
        Set<RoleModel> roles = new HashSet<>();
        RoleModel roleModel = new RoleModel(1);
        roleModel.setLevel(RoleConstant.LEVEL_3);
        roles.add(roleModel);
        clientModel.setRoles(roles);
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ArrayList<LeaveModel> leaveModels = new ArrayList<>();
        LeaveModel lm = new LeaveModel();
        lm.setId(1L);
        lm.setUser(clientModel);
        lm.setType(StatusConstant.PENDING_NO);
        leaveModels.add(lm);
        Page<LeaveModel> page = new PageImpl<>(leaveModels);
        Mockito.when(leaveRepository.findByStatus(Mockito.anyLong(), Mockito.anyInt(), Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> delete = leaveService.pendingList(new PageRequestParam());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, LeaveResponse.class)), delete);
    }

    @Test
    @DisplayName("待審核假單_搜部門_成功")
    void pendingList_department_ok() {
        ClientModel clientModel = new ClientModel(1);
        clientModel.setDepartment(new DepartmentModel(1L));
        Set<RoleModel> roles = new HashSet<>();
        RoleModel roleModel = new RoleModel(1);
        roleModel.setLevel(RoleConstant.LEVEL_1);
        roles.add(roleModel);
        clientModel.setRoles(roles);
        UserDetailImpl principal = new UserDetailImpl(clientModel, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ArrayList<LeaveModel> leaveModels = new ArrayList<>();
        LeaveModel lm = new LeaveModel();
        lm.setId(1L);
        lm.setUser(clientModel);
        lm.setType(StatusConstant.PENDING_NO);
        leaveModels.add(lm);
        Page<LeaveModel> page = new PageImpl<>(leaveModels);
        Mockito.when(leaveRepository.findByStatusAndDepartment(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.any())).thenReturn(page);
        ResponseEntity<ApiResponse> delete = leaveService.pendingList(new PageRequestParam());
        Assertions.assertEquals(ApiResponse.success(new PageResponse<>(page, LeaveResponse.class)), delete);
    }
}