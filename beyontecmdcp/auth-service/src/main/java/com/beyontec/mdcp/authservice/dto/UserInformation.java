package com.beyontec.mdcp.authservice.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserInformation {

	private String userName;

	private String userType;

	private Integer userId;

	private String userPhoto;
	
	private String fullName;

	private String firstName;

	private String lastName;

	private String token;

	private String refreshToken;

	private String isPasswordUpdated;

	private String loggedfirst;

	private Integer attempts;

	private String email;

	private String lastLoggedIn;

	private String userAccount;

	private Integer companyId;

	private String companyLogo;

	private String companyContactNo;
	
	private String companyName;
	
	private List<Integer> accessRights;
	
	private Integer branchId;

}
