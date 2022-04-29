package com.beyontec.mdcp.company.dto;

import lombok.Data;

@Data
public class CertificateIssuanceDto {
	
	private long issuedCertificates;
	private long pendingCertificates;
	private String issuerType;;

}
