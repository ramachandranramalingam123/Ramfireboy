package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.RolesModule;
import com.beyontec.mdcp.model.RolesModulesParent;

@Repository
public interface RolesModulesRepo extends JpaRepository<RolesModule, Integer> {

	public List<RolesModule> findByPortalAndModuleParent(String portal, RolesModulesParent rolesModulesParent);

	public RolesModule findByModuleId(Integer moduleId);
	
	public RolesModule findByPortalAndModuleLabel(String portal, String moduleLabel);

}
