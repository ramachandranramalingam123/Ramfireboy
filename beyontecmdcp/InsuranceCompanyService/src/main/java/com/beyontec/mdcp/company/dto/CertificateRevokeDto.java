package com.beyontec.mdcp.company.dto;

import lombok.Data;

@Data
public class CertificateRevokeDto {
	
	private Integer userId;
	
	private String certificateNo;
	
	private Integer reasonId;
	
	private String policyNumber;

	private String registartionNumber;
	
	private String chassisNumber;
}
