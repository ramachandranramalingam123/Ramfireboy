package com.beyontec.mdcp.company.dto;

import java.util.Date;

import lombok.Data;

@Data
public class IssuedCertificatesDetails {

	private String certificateSerialNumber;
	private String insured;
	private String policyNumber;
	private String status;
	private String commencingDate;
	private String commencingTime;
	private Date expiryDate;
	private String registrationNo;
	private String issuedBy;
	private String chassisNo;
	private String markType;
	private String qrCode;
	private String licensed;
	private String usage;
	private String signature;
	private String branch;
	private String transactionDate;
}
