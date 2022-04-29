package com.beyontec.mdcp.dto;

import lombok.Data;

@Data
public class CertificateAuthorityNotifyDto {
	
	private Integer companyId;
	
	private String companyName;
	
	private Long totalCertificate;
	
	private Integer allocationId;
	
	private Integer companyPage;
	
	private String paymentFileName;
	
	private String paymentDescription;

}
