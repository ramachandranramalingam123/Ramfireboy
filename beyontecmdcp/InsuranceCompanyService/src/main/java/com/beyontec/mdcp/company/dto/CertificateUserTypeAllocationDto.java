package com.beyontec.mdcp.company.dto;

import java.util.List;

import lombok.Data;

@Data
public class CertificateUserTypeAllocationDto {
	
	private List<CountDTO> userTypecertificates;
	
	private TotalCertificateDto totalCertificate;

}
