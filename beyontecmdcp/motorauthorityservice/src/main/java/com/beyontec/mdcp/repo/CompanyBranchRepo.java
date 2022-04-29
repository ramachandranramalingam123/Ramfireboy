package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.BranchMaster;
import com.beyontec.mdcp.model.CompanyBranch;
import com.beyontec.mdcp.model.InsuranceCompany;

@Repository
public interface CompanyBranchRepo extends JpaRepository<CompanyBranch, Integer>{

	public List<CompanyBranch> findByCompany(InsuranceCompany company);

	public CompanyBranch findByCompanyBranchId(Integer companyBranchId);

	public CompanyBranch findByCompanyAndBranch(InsuranceCompany companyData, BranchMaster branchMasterData);

	public CompanyBranch findByBranch(BranchMaster branchMasterData);

}
