package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.BranchMaster;
import com.beyontec.mdcp.model.InsuranceCompany;

@Repository
public interface BranchMasterRepo extends JpaRepository<BranchMaster, Integer> {
	
	public  BranchMaster findByBranchName(String branchName);

	public BranchMaster findByBranchNameAndCompany(String branchName, InsuranceCompany companyData);

	public List<BranchMaster> findByCompany(InsuranceCompany companyData);

	
}
