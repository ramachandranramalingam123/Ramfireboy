package com.beyontec.mdcp.company.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.company.dto.CertificateDTO;
import com.beyontec.mdcp.company.dto.CertificateIssuanceDto;
import com.beyontec.mdcp.company.dto.CertificateUserTypeAllocationDto;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.service.CertificateService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	
	@Autowired
	private CertificateService certificateService;
	
	@GetMapping("/certificates")
	public Response<CertificateDTO> getCertificateDetailsByCompany(@RequestParam("companyId") Integer companyId,
			@RequestParam("pageSize") int pageSize, @RequestParam("currentPage") int currentPage,
			@RequestParam(value = "filter", required = false) String filter, 
			@RequestParam("issuedBy") Integer issuedBy,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "branchId", required = false) Integer branchId,
			@RequestParam("value") String value) {

		return certificateService.getCertificateDetailsByCompany(companyId, pageSize, currentPage, 
				filter, issuedBy, startDate,endDate, branchId, value);
	}
	
	@GetMapping("/count")
	public Response<CertificateUserTypeAllocationDto> getCount(@RequestParam("companyId") Integer companyId) {
		return certificateService.getCount(companyId);
	}
	
	@GetMapping("/certificateIssuance")
	public Response<List<CertificateIssuanceDto>> getCertificateIssuance(@RequestParam("companyId") Integer companyId) {

		return certificateService.getCertificateIssuance(companyId);
	}


}
