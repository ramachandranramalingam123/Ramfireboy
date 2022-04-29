package com.beyontec.mdcp.dto;

import java.util.List;

import lombok.Data;

@Data
public class AllAuthorityUsersDto {
	
	List<UsersAuthorityDto> authorityusers;
	private Integer totalCount;

}
