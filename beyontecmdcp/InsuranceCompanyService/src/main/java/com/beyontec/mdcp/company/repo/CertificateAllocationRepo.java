package com.beyontec.mdcp.company.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.beyontec.mdcp.company.model.CertificateAllocation;
import com.beyontec.mdcp.company.model.CompanyUserType;
import com.beyontec.mdcp.company.model.InsuranceCompany;

@Repository
public interface CertificateAllocationRepo extends JpaRepository<CertificateAllocation, Integer>{

	public List<CertificateAllocation> findByShowCompanyNonify(int i);

	public CertificateAllocation findByAllocationId(Integer allocationId);

	public List<CertificateAllocation> findByShowCompanyNonifyAndCompany(int i, InsuranceCompany companyData);

	@Query("SELECT SUM(al.allocatedCertificates) from CertificateAllocation al WHERE al.company= :company AND al.cuIsrejected=0 AND al.allocatedBy is not null")	
	public Integer getSumOfAllocatedCertificate(@Param("company") InsuranceCompany company);

	@Query("SELECT SUM(al.allocatedCertificates) from CertificateAllocation al WHERE al.company= :company AND al.cuIsrejected=0 AND al.approvedBy is not null")	
	public Integer getSumOfAllocatedCertificateByCompany(@Param("company") InsuranceCompany insuranceCompany);

	public CertificateAllocation findByAllocationIdAndShowCompanyNonifyAndCompany(Integer allocationId, int i,
			InsuranceCompany companyData);

	public List<CertificateAllocation> findByCertificateNumStatus(int i);


}
