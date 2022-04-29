package com.beyontec.mdcp.authservice.dto;

import lombok.Data;

@Data
public class RefreshTokenDto {
	
	
	
	private String grantType;
	
	private String refreshToken;

}
