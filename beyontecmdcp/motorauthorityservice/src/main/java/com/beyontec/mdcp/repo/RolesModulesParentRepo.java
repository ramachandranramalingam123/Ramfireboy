package com.beyontec.mdcp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.RolesModulesParent;

@Repository
public interface RolesModulesParentRepo extends JpaRepository<RolesModulesParent, Integer> {

	public RolesModulesParent findByModuleParent(String moduleParent);
}
