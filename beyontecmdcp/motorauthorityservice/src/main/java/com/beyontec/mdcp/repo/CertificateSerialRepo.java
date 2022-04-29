package com.beyontec.mdcp.repo;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.CertificateSerialNum;
import com.beyontec.mdcp.model.InsuranceCompany;

@Repository
public interface CertificateSerialRepo extends JpaRepository<CertificateSerialNum, Integer>{

	public  boolean  existsByCompany(InsuranceCompany company);
	
	public CertificateSerialNum findFirstByCompanyOrderBySerialNumOrderDesc(InsuranceCompany company);
	
	public Long countByAllocatedDate(LocalDate date);

	public Long countByCompany(InsuranceCompany company);
	
	

	
}
