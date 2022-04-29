package com.beyontec.mdcp.company.dto;

import lombok.Data;

@Data
public class CertificateCompanyNotifyDto {
	
    private Integer companyId;
	
	private String companyName;
	
	private Long totalCertificate;
	
	private Integer allocationId;

}
