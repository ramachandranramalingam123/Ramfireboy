package com.beyontec.mdcp.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.CertificateDetails;
import com.beyontec.mdcp.model.InsuranceCompany;

@Repository
public interface CertificateRepo extends JpaRepository<CertificateDetails, String> {

	List<CertificateDetails> findAllByInsuranceCompanyOrderByCreatedDateDesc(InsuranceCompany insuranceCompany, Pageable pageable);
	
	List<CertificateDetails> findAllByInsuranceCompanyAndCreatedDateBetweenOrderByCreatedDateDesc(InsuranceCompany insuranceCompany,
			LocalDateTime date1, LocalDateTime date2, Pageable pageable);
	
	@Query("select count(c) from CertificateDetails c where c.insuranceCompany=:insuranceCompany")
	public Long countByCompany(@Param("insuranceCompany") InsuranceCompany company);

	List<CertificateDetails> findAllByInsuranceCompany(InsuranceCompany insuranceCompany);

	@Query("select count(c) from CertificateDetails c")
	public Long countByIssuedCerts();

}
