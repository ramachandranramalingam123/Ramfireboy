package com.beyontec.mdcp.company.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.BranchMaster;
import com.beyontec.mdcp.company.model.InsuranceCompany;



@Repository
public interface BranchMasterRepo extends JpaRepository<BranchMaster, Integer> {

	public  BranchMaster findByBranchName(String branchName);

	public BranchMaster findByBranchNameAndCompany(String string, InsuranceCompany companyData);
	
	public List<BranchMaster> findByCompany(InsuranceCompany companyData);

	@Query("select branchMaster from BranchMaster branchMaster where branchMaster.branchCode=:branchCode AND branchMaster.company=:company")
	public BranchMaster findByBranchCodeAndCompany(@Param("branchCode") String branchCode, @Param("company") InsuranceCompany company);
}
