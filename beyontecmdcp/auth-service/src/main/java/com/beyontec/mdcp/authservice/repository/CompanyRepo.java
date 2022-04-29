package com.beyontec.mdcp.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.authservice.model.InsuranceCompany;

@Repository
public interface CompanyRepo extends JpaRepository<InsuranceCompany, String> {

	public InsuranceCompany findByCompanyId(Integer companyId);

}
