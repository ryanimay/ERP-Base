package com.erp.base.repository;

import com.erp.base.model.entity.MenuModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuModel, Long> {
    @Query("SELECT m FROM MenuModel m WHERE m.status ORDER BY m.level DESC, m.parent.id, m.orderNum ")
    List<MenuModel> findAllOrderByParentsAndOrderNum();
    @Query("SELECT m FROM MenuModel m JOIN m.roles r WHERE m.status AND r.id = :id ORDER BY m.level DESC, m.parent.id, m.orderNum")
    List<MenuModel> findByRoleId(Long id);
}
