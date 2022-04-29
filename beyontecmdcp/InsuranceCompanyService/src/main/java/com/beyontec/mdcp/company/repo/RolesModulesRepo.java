package com.beyontec.mdcp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.RolesModule;

@Repository
public interface RolesModulesRepo extends JpaRepository<RolesModule, Integer> {

	public RolesModule findByPortalAndModuleLabel(String portal, String moduleLabel);

}
