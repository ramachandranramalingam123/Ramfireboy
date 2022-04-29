package com.beyontec.mdcp.authservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.authservice.model.CertificateDetails;

@Repository
public interface CertificateRepo extends JpaRepository<CertificateDetails, Integer> {

	public CertificateDetails findByCertificateSerialNumber(String serialNo);
	
	public List<CertificateDetails> findByPrimaryEmail(String email);
	
	public List<CertificateDetails> findByPolicyNumber(String policyNo);

}
