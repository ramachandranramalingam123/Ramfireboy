package com.beyontec.mdcp.dto;

import java.util.List;

import lombok.Data;

@Data
public class InsuranceCompanies {

	private List<InsuranceCompanyListDTO> insuranceCompanies;
	
	private long totalCompanies;
	
	private Integer companyPage;

}
