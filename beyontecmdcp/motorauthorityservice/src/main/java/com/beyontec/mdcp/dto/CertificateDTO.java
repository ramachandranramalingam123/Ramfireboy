package com.beyontec.mdcp.dto;

import java.util.List;

import lombok.Data;

@Data
public class CertificateDTO {

	private Long issuedCertificates;
	private Long pendingCertificates;
	private Long allocatedCertificates;
	private Long totalCertificateByAuthority;
	private List<IssuedCertificatesDetails> issuedCertificatesDetails;
	private Long totalCertificates;

}
