package com.beyontec.mdcp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.RevokeReason;

@Repository
public interface RevokeReasonRepo extends JpaRepository<RevokeReason, Integer>{

	public RevokeReason findByReasonId(Integer reasonId);
	
	public RevokeReason findByReason(String reason);

}
