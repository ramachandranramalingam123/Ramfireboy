package com.beyontec.mdcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.dto.AutoApprovalDto;
import com.beyontec.mdcp.dto.CompanyAddDto;
import com.beyontec.mdcp.dto.CompanyCertificateStatusDto;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.service.CompanyService;

@RestController
@RequestMapping("/company")
public class CompanyController {
	
	@Autowired
	private CompanyService companyService;
	
	@PostMapping("/add")
	public Response<String> companyCreation(@RequestBody CompanyAddDto companyAddDto) {

		return companyService.addCompanyDetails(companyAddDto);

	}

	@PutMapping("/editAutoApproval")
	public Response<String> updateAutoApprovalLimitAndTime(@RequestBody List<AutoApprovalDto> autoApprovalDto) {

		return companyService.editAutoApprovals(autoApprovalDto);
	}
	
	@DeleteMapping("/remove")
	public Response<String> removeUser(@RequestParam Integer companyId, @RequestHeader Integer loginId) {

		return companyService.deleteCompany(companyId, loginId);

	}
	
	@GetMapping("/certificate/status")
	public Response<CompanyCertificateStatusDto> CompanyCertificateStaus() {

		return companyService.companyCertificateStatusDetails();

	}
	
}
