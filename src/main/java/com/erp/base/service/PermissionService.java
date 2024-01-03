package com.erp.base.service;

import com.erp.base.dto.request.permission.BanRequest;
import com.erp.base.dto.request.permission.PermissionTreeResponse;
import com.erp.base.dto.request.permission.SecurityConfirmRequest;
import com.erp.base.dto.response.ApiResponse;
import com.erp.base.dto.security.RolePermissionDto;
import com.erp.base.enums.response.ApiResponseCode;
import com.erp.base.model.PermissionModel;
import com.erp.base.repository.PermissionRepository;
import com.erp.base.service.cache.ClientCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class PermissionService {
    @Value("${security.password}")
    private String securityPassword;
    private PermissionRepository permissionRepository;
    private ClientCache clientCache;
    private RoleService roleService;
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }
    @Autowired
    public void setClientCache(ClientCache clientCache) {
        this.clientCache = clientCache;
    }
    @Autowired
    public void setPermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }


    public List<PermissionModel> findAll() {
        return permissionRepository.findAll();
    }

    //排序由子->父
//    private List<PermissionModel> sortPermissions(List<PermissionModel> permissions){
//        Map<Long, List<PermissionModel>> map = getParentChildMap(permissions);
//        return bfsSort(map);
//    }

    //按分層排序，用bfs
//    private List<PermissionModel> bfsSort(Map<Long, List<PermissionModel>> map) {
//        List<PermissionModel> resultList = new ArrayList<>(map.size());
//        Queue<PermissionModel> queue = new ConcurrentLinkedQueue<>();
//        List<PermissionModel> rootPermissionModels = map.get(0L);//根節點從parentsId:0開始
//        if(map.containsKey(0L) && !rootPermissionModels.isEmpty()){
//            queue.addAll(rootPermissionModels);//根節點放進que
//        }
//        while(!queue.isEmpty() && !map.isEmpty()){
//            PermissionModel model = queue.poll();
//            long id = model.getId();
//            if(map.containsKey(id)){
//            List<PermissionModel> childNodes = map.get(id);
//                childNodes.forEach(node -> {
////                    另一種方法:
////                    Set<String> authoritiesIncludeParents = new HashSet<>(model.getAuthoritiesIncludeParents());
////                    authoritiesIncludeParents.add(node.getAuthority());
////                    node.setAuthoritiesIncludeParents(authoritiesIncludeParents);
////                    queue.offer(node);//把該節點下一層子節點放進que
//
//                    Set<String> authoritiesIncludeParents = node.getAuthoritiesIncludeParents();
//                    authoritiesIncludeParents.addAll(model.getAuthoritiesIncludeParents());
//                    node.setAuthoritiesIncludeParents(authoritiesIncludeParents);
//                    queue.offer(node);//把該節點下一層子節點放進que
//                });
//            }
//            map.remove(id);
//            resultList.add(model);//從頭部開始取出放入result
//        }
//        return resultList;
//    }

//    private Map<Long, List<PermissionModel>> getParentChildMap(List<PermissionModel> permissions){
//        Map<Long, List<PermissionModel>> map = new ConcurrentHashMap<>();
//        //把每個節點的父節點當key，value為所有對應的子節點Map<父, List<子>()>
//        for(PermissionModel model : permissions){
//            long parentsId = model.getParentId();
//            map.putIfAbsent(parentsId, new ArrayList<>());
//            map.get(parentsId).add(model);
//        }
//        return map;
//    }

    public ResponseEntity<ApiResponse> getRolePermission(long roleId) {
        PermissionTreeResponse permissionTree = clientCache.getPermissionTree();
        Map<String, Object> map = new HashMap<>();
        map.put("tree", permissionTree);

        Set<RolePermissionDto> rolePermission = clientCache.getRolePermission(roleService.findById(roleId));
        List<Long> rolePermissionList = rolePermission.stream().map(RolePermissionDto::getId).toList();
        map.put("rolePermissions", rolePermissionList);
        return ApiResponse.success(ApiResponseCode.SUCCESS, map);
    }

    //從緩存拿
    public ResponseEntity<ApiResponse> getPermissionTreeCache() {
        PermissionTreeResponse permissionTree = clientCache.getPermissionTree();
        return ApiResponse.success(ApiResponseCode.SUCCESS, permissionTree);
    }

//    public PermissionTreeResponse getPermissionTree() {
//        List<PermissionModel> allPermission = permissionRepository.findAll();
//        Map<Long, List<PermissionModel>> parentChildMap = getParentChildMap(allPermission);
//        PermissionModel node = parentChildMap.get(0L).get(0);
//        parentChildMap.remove(0L);
//        PermissionTreeResponse treeRoot = new PermissionTreeResponse(node);
//        buildPermissionTree(treeRoot, parentChildMap);
//        treeRoot.sortChildTree();
//        return treeRoot;
//    }

    private void buildPermissionTree(PermissionTreeResponse parentNode, Map<Long, List<PermissionModel>> map){
        long id = parentNode.getId();
        map.computeIfPresent(id, (key, childNodes) -> {
            childNodes.forEach(childModel -> {
                PermissionTreeResponse childNode = new PermissionTreeResponse(childModel);
                parentNode.addChildTree(childNode);
                buildPermissionTree(childNode, map);
            });
            return null;
        });
    }

    public ResponseEntity<ApiResponse> ban(BanRequest request) {
        permissionRepository.updateStatusById(request.getId(), request.isStatus());
        clientCache.refreshPermission();
        return ApiResponse.success(ApiResponseCode.SUCCESS);
    }

    public ResponseEntity<ApiResponse> securityConfirm(SecurityConfirmRequest request) {
        if(securityPassword.equals(request.getSecurityPassword())) return ApiResponse.success(ApiResponseCode.SUCCESS, true);
        return ApiResponse.error(ApiResponseCode.SECURITY_ERROR, false);
    }

    public ApiResponseCode checkPermissionIfDeny(String requestedUrl) {
        Boolean result = permissionRepository.checkPermissionIfDeny(requestedUrl);
        if(!result) return ApiResponseCode.ACCESS_DENIED;
        return null;
    }
}