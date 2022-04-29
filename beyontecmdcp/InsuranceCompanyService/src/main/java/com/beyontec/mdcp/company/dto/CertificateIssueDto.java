package com.beyontec.mdcp.company.dto;

import lombok.Data;

@Data
public class CertificateIssueDto {

	private Integer companyId;

	private String certificateNo;

	private String policyHolder;

	private String policyNumber;

	private String commencingDate;

	private String expiryDate;

	private String registrationNo;

	private String chassisNo;

	private String licensed;

	private String vehicleType;

	private String issuedBy;

	private Integer userId;

	private String approvedBy;

	private String email;

	private String usage;

	private String qrCode;

	private String intermediary;

	private String intermediaryIRA;

	private Double sumInsured;
	
	private Integer companyBranchId;
	
	private String signature;
	
	private String companyCode;
	
	private String branchCode;
}
