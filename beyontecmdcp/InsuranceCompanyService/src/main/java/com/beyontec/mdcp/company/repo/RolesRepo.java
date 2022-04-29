package com.beyontec.mdcp.company.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.Roles;
import com.beyontec.mdcp.company.model.RolesMaster;
import com.beyontec.mdcp.company.model.RolesModule;

@Repository
public interface RolesRepo extends JpaRepository<Roles, Integer> {

	public List<Roles> findByRolesModule(RolesModule rolesModule);
	
	public List<Roles> findByRolesMaster(RolesMaster rolesMaster);
}
