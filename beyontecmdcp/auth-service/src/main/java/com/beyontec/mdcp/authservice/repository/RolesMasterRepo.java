package com.beyontec.mdcp.authservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beyontec.mdcp.authservice.model.RolesMaster;

public interface RolesMasterRepo extends JpaRepository<RolesMaster, Integer>{
	
	
	public RolesMaster findByRoleId(String roleId);
	
	public RolesMaster findByMasterId(Integer roleId);
	
	public Page<RolesMaster> findByUserPortal(String userPortal, Pageable pageable);
	
	
	@Query("Select master from RolesMaster master where master.userPortal=:userPortal")
	public List<RolesMaster> findByUserportal(String userPortal);
}
