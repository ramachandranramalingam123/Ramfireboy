package com.beyontec.mdcp.company.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.BranchMaster;
import com.beyontec.mdcp.company.model.CompanyBranch;
import com.beyontec.mdcp.company.model.InsuranceCompany;



@Repository
public interface CompanyBranchRepo extends JpaRepository<CompanyBranch, Integer>{

	public List<CompanyBranch> findByCompany(InsuranceCompany company);

	public CompanyBranch findByCompanyBranchId(Integer companyBranchId);

	public CompanyBranch findByCompanyAndBranch(InsuranceCompany companyData, BranchMaster branchMasterData);

	public List<CompanyBranch> findByCompanyBranchIdIn(List<Integer> branchId);

	public CompanyBranch findByBranch(BranchMaster branchMaster);
}
