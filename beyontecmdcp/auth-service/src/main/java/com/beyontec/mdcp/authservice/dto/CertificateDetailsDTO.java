package com.beyontec.mdcp.authservice.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CertificateDetailsDTO {

	private Integer certificateId;

	private String certificateSerialNumber;

	private String insured;

	private String policyNumber;

	private String status;

	private String commencingDate;
	
	private String commencingTime;

	private Date expiryDate;

	private String registartionNumber;

	private String chassisNumber;

	private String vechicleType;

	private String licensed;

	private String issuedBy;

	private String revokeReason;
	
	private String qrCode;
	
	private String usage;


}
