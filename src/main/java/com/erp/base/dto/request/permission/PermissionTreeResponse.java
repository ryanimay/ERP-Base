package com.erp.base.dto.request.permission;

import com.erp.base.model.PermissionModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionTreeResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -5831980508981731523L;
    private long id;
    private String info;
    private List<PermissionTreeResponse> childTree;

    public PermissionTreeResponse(PermissionModel model) {
        this.id = model.getId();
        this.info = model.getInfo();
        childTree = new ArrayList<>();
    }

    //遞迴排序所有分支
    public void sortChildTree(){
//        childTree.sort(Comparator.comparing(PermissionTreeResponse::getOrder, Comparator.nullsLast(Comparator.naturalOrder())));
//        childTree.forEach(PermissionTreeResponse::sortChildTree);
    }

    public void addChildTree(PermissionTreeResponse node) {
        childTree.add(node);
    }
}
