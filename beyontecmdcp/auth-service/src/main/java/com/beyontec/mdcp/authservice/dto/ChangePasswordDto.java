package com.beyontec.mdcp.authservice.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {

	private String userName;
	private String newPassword;
	private String oldPassword;
}
