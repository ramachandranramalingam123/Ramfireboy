package com.beyontec.mdcp.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.authservice.model.BranchMaster;
import com.beyontec.mdcp.authservice.model.CompanyUserBranch;
import com.beyontec.mdcp.authservice.model.User;



@Repository
public interface CompanyUserBranchRepo extends JpaRepository<CompanyUserBranch, Integer>{

	public CompanyUserBranch findByBranch(BranchMaster branch);

	public CompanyUserBranch findByUserAndPrimaryBranch(User userDetail, String string);

}
