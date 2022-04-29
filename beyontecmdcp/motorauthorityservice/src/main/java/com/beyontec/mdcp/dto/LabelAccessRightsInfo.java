package com.beyontec.mdcp.dto;

import lombok.Data;

@Data
public class LabelAccessRightsInfo {
	
	private String moduleLabel;

	private Boolean canAccess;
	
	private Integer moduleId;

}
