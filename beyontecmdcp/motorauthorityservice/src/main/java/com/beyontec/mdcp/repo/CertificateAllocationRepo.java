package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.beyontec.mdcp.model.CertificateAllocation;
import com.beyontec.mdcp.model.InsuranceCompany;
import org.springframework.stereotype.Repository;



@Repository
public interface CertificateAllocationRepo extends JpaRepository<CertificateAllocation, Integer>{
	
	public List<CertificateAllocation> findByShowAuthorityNonify(Integer value);

	public CertificateAllocation findByAllocationId(Integer allocationId);

	public List<CertificateAllocation> findByShowAuthorityNonifyAndRequestedDateNotNull(int value);

	
	@Query("SELECT SUM(al.allocatedCertificates) from CertificateAllocation al WHERE al.company= :company AND al.cuIsrejected=0 AND al.approvedBy is not null")	
	public Long getSumOfAllocatedCertificate(@Param("company") InsuranceCompany company);
	
	@Query("SELECT SUM(al.allocatedCertificates) from CertificateAllocation al WHERE al.company= :company AND al.cuIsrejected=0 AND al.allocatedBy is not null")	
	public Long getSumOfAllocatedCertificateByAuthority(@Param("company") InsuranceCompany company);
	
	@Query("SELECT SUM(al.requestedCertificates) from CertificateAllocation al WHERE al.isrejected=0 AND al.allocatedBy is null")	
	public Long getSumOfAuthorityToBeAllocateCertificate();

	public CertificateAllocation findByAllocationIdAndShowAuthorityNonify(Integer allocationId, int i);


	@Query("SELECT SUM(al.allocatedCertificates) from CertificateAllocation al WHERE al.allocatedBy is not null")	
	public Long getTotalllocatedCertificates();
	
	@Query("SELECT SUM(al.allocatedCertificates) from CertificateAllocation al WHERE al.isrejected=0 AND al.allocatedBy is not null AND al.company IN (:companies)")	
	public Long getTotalllocatedCertificatesByStatus(List<InsuranceCompany> companies);
	
}
