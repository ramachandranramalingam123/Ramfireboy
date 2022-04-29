package com.beyontec.mdcp.company.dto;

import lombok.Data;

@Data
public class SingleAllocationDto {
	
	private Integer companyId;
	
	private String allocateFor;
	
	private Integer allocateCount;

}
