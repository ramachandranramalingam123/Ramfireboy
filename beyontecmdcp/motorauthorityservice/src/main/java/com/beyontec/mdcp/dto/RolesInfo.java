package com.beyontec.mdcp.dto;

import com.beyontec.mdcp.model.AccessRightsMap;

import lombok.Data;

@Data
public class RolesInfo {

	private String roleId;

	private String roleName;

	private AccessRightsMap accessRightsMap;
	
	
}
