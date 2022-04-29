package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beyontec.mdcp.model.RolesMaster;

public interface RolesMasterRepo extends JpaRepository<RolesMaster, Integer>{
	
	
	public RolesMaster findByRoleId(String roleId);
	
	public RolesMaster findByMasterId(Integer roleId);
	
	@Query("Select master from RolesMaster master where master.userPortal=:userPortal and master.companyId is null")
	public Page<RolesMaster> findByUserPortal(String userPortal, Pageable pageable);
	
	@Query("Select master from RolesMaster master where master.userPortal=:userPortal and master.companyId is null")
	public List<RolesMaster> findByUserportal(String userPortal);
	
	public Page<RolesMaster> findByCompanyId(String companyId, Pageable pageable);
	
	public List<RolesMaster> findByCompanyId(String companyId);
}
