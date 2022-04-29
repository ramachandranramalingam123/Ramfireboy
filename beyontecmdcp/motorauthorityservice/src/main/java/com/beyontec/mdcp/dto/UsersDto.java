package com.beyontec.mdcp.dto;

import lombok.Data;

@Data
public class UsersDto {

	private int userId;
	private String userName;
	private String firstName;
	private String companyName;
	private Integer companyId;
	private String status;
}
