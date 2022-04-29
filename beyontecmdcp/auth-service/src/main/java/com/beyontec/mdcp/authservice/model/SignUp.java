package com.beyontec.mdcp.authservice.model;

import lombok.Data;

@Data
public class SignUp {

	private String dob;

	private String mobileNo;

	private String policyNo;

	private String emailId;

	private String userId;

	private String password;

	private String confirmPassword;

}
