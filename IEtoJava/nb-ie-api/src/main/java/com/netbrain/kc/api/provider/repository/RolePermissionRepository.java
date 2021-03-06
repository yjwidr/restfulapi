package com.netbrain.kc.api.provider.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.netbrain.kc.api.model.datamodel.RolePermissionEntity;  
  
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, String>,JpaSpecificationExecutor<RolePermissionEntity>,Serializable{  
    @Query(value = "select u.permissionId from RolePermissionEntity u where u.roleId=?1")
    List<String> findByRoleId(String RoleId);  
} 