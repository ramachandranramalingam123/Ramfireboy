package com.beyontec.mdcp.dto;

import lombok.Data;

@Data
public class InsuranceCompanyListDTO {

	private Integer companyId;
	private String companyName;
	private String branchName;
	private Long totalCertificateByAuthority;
	private Long totalCertificates;
	private String code;
	private String address;

	private String address1;
	private String address2;
	private String city;
	private String country;
	private String mailId;
	private String postBoxCode;
	private String state;
	private String companyLogo;

	private String contactNumber;
	private Integer autoApprovalLimit;
	private Long autoApprovalTime;
	

}
