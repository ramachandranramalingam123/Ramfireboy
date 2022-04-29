package com.beyontec.mdcp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.CertificateDetails;
import com.beyontec.mdcp.company.model.CertificateRevoke;

@Repository
public interface CertificateRevokeRepo extends JpaRepository<CertificateRevoke, Integer> {

	public CertificateRevoke findByCertificateDetails(CertificateDetails certificateDetails);

}
