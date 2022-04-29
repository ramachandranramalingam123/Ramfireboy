package com.beyontec.mdcp.authservice.dto;

import lombok.Data;

@Data
public class RefTokenResDto {
	
	private String userName;

	private String userType;
	
	private Integer userId;

	private String token;
	
	private String refreshToken;
	
	private String email;

	private String brokerId;

}
