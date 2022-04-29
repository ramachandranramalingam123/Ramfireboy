package com.beyontec.mdcp.authservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.authservice.model.CompanyBranch;
import com.beyontec.mdcp.authservice.model.InsuranceCompany;





@Repository
public interface CompanyBranchRepo extends JpaRepository<CompanyBranch, Integer>{

	public List<CompanyBranch> findByCompany(InsuranceCompany company);

	public CompanyBranch findByCompanyBranchId(Integer companyBranchId);

}
