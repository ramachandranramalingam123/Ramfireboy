package com.beyontec.mdcp.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class UserRolesDto {

	private String roleId;

	private String roleName;

	private Map<String, List<AccessRightsInfo>> accessRightsInfoMap;

	private String userPortal;

	private String companyId;
}
