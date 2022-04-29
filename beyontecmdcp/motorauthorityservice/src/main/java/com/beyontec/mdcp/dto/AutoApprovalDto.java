package com.beyontec.mdcp.dto;

import lombok.Data;

@Data
public class AutoApprovalDto {
	
	private Integer companyId;
	private  Integer appriovalLimit;
	private Long approvalTime;

}
