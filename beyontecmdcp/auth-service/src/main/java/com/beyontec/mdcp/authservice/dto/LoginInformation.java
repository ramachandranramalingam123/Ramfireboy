package com.beyontec.mdcp.authservice.dto;

import lombok.Data;

@Data
public class LoginInformation {

	private String token;

	private String refreshToken;
}
