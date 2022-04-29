package com.beyontec.mdcp.dto;

import java.util.List;

import lombok.Data;

@Data
public class CompanyUserDto {

	private Integer userId;

	private String firstName;

	private String middleName;

	private String lastName;

	private String mobileNumber;

	private String userName;

	private String password;

	private String email;

	private String desingnation;

	private String insureCompany;
	
	private Integer companyId;
	
	private Integer userTypeId;
	
	private String signature;
	
	private String photo;
	
	private Integer roleId;
	
    private List<Integer> companyBranchId;
	
	private Integer primaryCompanyBranchId;
	
	private Integer createdBy;
	

}
