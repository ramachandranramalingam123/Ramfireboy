package com.beyontec.mdcp.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.authservice.model.BranchMaster;




@Repository
public interface BranchMasterRepo extends JpaRepository<BranchMaster, Integer> {

	public  BranchMaster findByBranchName(String branchName);

}
