package com.beyontec.mdcp.company.repo;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.CertificateSerialNum;
import com.beyontec.mdcp.company.model.InsuranceCompany;


@Repository
public interface CertificateSerialRepo extends JpaRepository<CertificateSerialNum, Integer>{

	public  CertificateSerialNum  findTopByOrderBySerialNumOrderDesc();
	
	public CertificateSerialNum findFirstByCompanyOrderBySerialNumOrderDesc(InsuranceCompany company);
	
	public CertificateSerialNum findFirstByCompanyOrderBySerialNumOrderAsc(InsuranceCompany company);
	
	public Long countByAllocatedDate(LocalDate date);

	public Long countByCompany(InsuranceCompany company);

	public CertificateSerialNum findFirstByCompanyAndIssuedStatusOrderBySerialNumOrderAsc(
			InsuranceCompany insuranceCompany, String string);

	public CertificateSerialNum findBySerialNum(String certificateNo);
	
	@Query("SELECT count(cn) FROM CertificateSerialNum cn")
	public Long getCountCertSerialOrder();
		
}
