package com.beyontec.mdcp.dto;

import lombok.Data;

@Data
public class CompanyCertificateStatusDto {
		
	private Long allocatedCertificates;
	
	private Long issuedCertificates;
	
	private Long pendingCertificates;

}
