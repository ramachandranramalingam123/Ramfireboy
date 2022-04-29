package com.beyontec.mdcp.dto;


import java.util.Date;

import lombok.Data;

@Data
public class IssuedCertificatesDetails {

	private String certificateSerialNumber;
	private String insured;
	private String policyNumber;
	private String status;
	private Date commencingDate;
	private Date expiryDate;
	private String registrationNo;
	private String chassisNo;
	private String markType;
	private String issuedBy;
	private String qrCode;
	private String licensed;
	private String usage;
	private String signature;

}
