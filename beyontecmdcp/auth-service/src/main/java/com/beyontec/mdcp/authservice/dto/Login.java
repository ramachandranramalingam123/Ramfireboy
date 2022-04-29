package com.beyontec.mdcp.authservice.dto;

import lombok.Data;

@Data
public class Login {
	
	private String username;
	
	private String password;
	
	private String grantType;
	

}
