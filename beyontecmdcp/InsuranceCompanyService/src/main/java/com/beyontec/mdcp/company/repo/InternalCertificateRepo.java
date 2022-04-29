package com.beyontec.mdcp.company.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.CompanyUserType;
import com.beyontec.mdcp.company.model.InsuranceCompany;
import com.beyontec.mdcp.company.model.InternalCertificate;

@Repository
public interface InternalCertificateRepo extends JpaRepository<InternalCertificate, Integer>{
	
	@Query("SELECT SUM(ic.allocatedCertificates) from InternalCertificate ic WHERE ic.company= :company AND ic.companyUserType= :companyUserType")	
	public Integer getSumOfAllocatedCertificate(@Param(value = "company")InsuranceCompany insuranceCompany,
			@Param(value = "companyUserType")CompanyUserType companyUserType);
	
	@Query("SELECT SUM(ic.allocatedCertificates) from InternalCertificate ic WHERE ic.company= :company")	
	public Integer getSumOfAllocatedCertificateByAll(@Param(value = "company")InsuranceCompany insuranceCompany);

	public InternalCertificate findByCompanyAndCompanyUserType(InsuranceCompany company, CompanyUserType companyUserType);

	public InternalCertificate  findByCompany(InsuranceCompany company);

	@Query("SELECT ic from InternalCertificate ic WHERE "
			+ "ic.company= :company AND ic.companyUserType= :companyUserType AND ic.allocatedCertificates > ?0")	
	public List<InternalCertificate> getAllocatedCertificate(InsuranceCompany company, CompanyUserType companyUserType);

}
