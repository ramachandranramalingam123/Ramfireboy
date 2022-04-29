package com.beyontec.mdcp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.InsuranceCompany;



@Repository
public interface CompanyRepo extends JpaRepository<InsuranceCompany, String> {

	public InsuranceCompany findByCompanyId(Integer companyId);

	public InsuranceCompany findByCompanyName(String companyName);

	public InsuranceCompany findByCompanyCode(String companyCode);
	
 

}
