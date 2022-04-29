package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.Roles;
import com.beyontec.mdcp.model.RolesMaster;
import com.beyontec.mdcp.model.RolesModule;

@Repository
public interface RolesRepo extends JpaRepository<Roles, Integer>{
	
	public List<Roles> findByRolesMaster(RolesMaster rolesMaster);
	
	@Query("SELECT DISTINCT role.rolesMaster from Roles role")
	public Page<RolesMaster> findByDistinctRolesMaster(Pageable pagable);
	
	@Query("SELECT DISTINCT role.rolesMaster from Roles role")
	public List<RolesMaster> findByDistinctRolesmaster();
	
	public List<Roles> findByRolesModule(RolesModule rolesModule);
	
	public Roles findByRolesMasterAndRolesModule(RolesMaster rolesMaster, RolesModule rolesModule);
	
	public List<Roles> findByRolesModuleAndRolesMaster(RolesModule rolesModule, RolesMaster rolesMaster);

}
