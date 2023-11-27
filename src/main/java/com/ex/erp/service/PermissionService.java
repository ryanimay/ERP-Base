package com.ex.erp.service;

import com.ex.erp.model.PermissionModel;
import com.ex.erp.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class PermissionService {
    private final PermissionRepository permissionRepository;
    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }


    public List<PermissionModel> findAll() {
        List<PermissionModel> allPermission = permissionRepository.findAll();
        return sortPermissions(allPermission);
    }

    //排序由子->父
    private List<PermissionModel> sortPermissions(List<PermissionModel> permissions){
        Map<Long, List<PermissionModel>> map = new HashMap<>();
        //把每個節點的父節點當key，value為所有對應的子節點
        for(PermissionModel model : permissions){
            long parentsId = model.getParentId();
            map.putIfAbsent(parentsId, new ArrayList<>());
            map.get(parentsId).add(model);
        }
        return bfsSort(map);
    }

    //按分層排序，用bfs
    private List<PermissionModel> bfsSort(Map<Long, List<PermissionModel>> map) {
        List<PermissionModel> resultList = new ArrayList<>();
        Queue<PermissionModel> queue = new LinkedList<>();
        List<PermissionModel> rootPermissionModels = map.get(0L);//根節點從parentsId:0開始
        if(map.containsKey(0L) && !rootPermissionModels.isEmpty()){
            queue.addAll(rootPermissionModels);//根節點放進que
        }
        while(!queue.isEmpty() && !map.isEmpty()){
            PermissionModel model = queue.poll();
            resultList.add(model);//從頭部開始取出放入result
            long id = model.getId();
            if(map.containsKey(id)){
                queue.addAll(map.get(id));//把該節點下一層子節點放進que
                map.remove(id);
            }
        }
        return resultList;
    }
}
