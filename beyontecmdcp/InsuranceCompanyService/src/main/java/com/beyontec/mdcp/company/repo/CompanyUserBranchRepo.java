package com.beyontec.mdcp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.BranchMaster;
import com.beyontec.mdcp.company.model.CompanyUserBranch;
import com.beyontec.mdcp.company.model.User;


@Repository
public interface CompanyUserBranchRepo extends JpaRepository<CompanyUserBranch, Integer>{

	public CompanyUserBranch findByBranchAndUser(BranchMaster branch, User user);

}
