package com.beyontec.mdcp.dto;

import java.util.List;

import lombok.Data;

@Data
public class BranchDataDto {
	
	List<CompanyBranchDto> CompanyBranch;
	
	private Long totalElements;

}
