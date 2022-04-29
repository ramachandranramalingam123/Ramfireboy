package com.beyontec.mdcp.dto;

import lombok.Data;

@Data
public class AuthorityUserDto {
	

	private Integer userId;

	private String firstName;

	private String middleName;

	private String lastName;

	private String mobileNumber;

	private String userName;

	private String password;

	private String email;

	private String desingnation;
	
	private Integer roleId;

	private Integer createdBy;
	
	private String photo;

}
