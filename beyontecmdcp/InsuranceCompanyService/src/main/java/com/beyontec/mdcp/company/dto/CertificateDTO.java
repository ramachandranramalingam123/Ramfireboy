package com.beyontec.mdcp.company.dto;

import java.util.List;

import lombok.Data;

@Data
public class CertificateDTO {

	private List<IssuedCertificatesDetails> issuedCertificatesDetails;
	private long totalCertificates;
}
