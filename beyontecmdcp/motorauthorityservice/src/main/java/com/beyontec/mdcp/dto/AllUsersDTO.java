package com.beyontec.mdcp.dto;

import java.util.List;

import lombok.Data;

@Data
public class AllUsersDTO {
	
	private List<UsersDto> usersDto;
	private Integer totalCount;

}
