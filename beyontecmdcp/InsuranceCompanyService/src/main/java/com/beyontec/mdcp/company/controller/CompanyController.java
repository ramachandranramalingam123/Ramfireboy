package com.beyontec.mdcp.company.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.company.dto.CertificateCompanyNotifyDto;
import com.beyontec.mdcp.company.dto.CompanyAddDto;
import com.beyontec.mdcp.company.dto.CompanyBranchAddDto;
import com.beyontec.mdcp.company.dto.CompanyUserTypesDto;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.service.CompanyService;


@RestController
@RequestMapping("/company")
public class CompanyController {
	
	@Autowired
	private CompanyService companyService;
	
	@GetMapping("/notify/certificate")
	public Response<List<CertificateCompanyNotifyDto>> certificateAuthorityNotify(@RequestParam Integer companyId, @RequestHeader Integer userId){
			
			return companyService.companyNotiifyCertificates(userId, companyId);
			
		}
	
	@PutMapping("/notify/reject")
	public Response<Integer> certificateAuthorityNotifyReject(@RequestParam Integer allocationId, @RequestParam Integer companyId, @RequestHeader Integer userId){
			
			return companyService.rejectCompanyNotiifyCertificates(allocationId,companyId,userId);
			
		}
	
	

	@PutMapping("/approval/certificate")
	public Response<String> certificateApproval(@RequestBody CertificateCompanyNotifyDto certificateCompanyNotifyDto,
			@RequestHeader Integer userId){
		
		return companyService.approvalCertificate(certificateCompanyNotifyDto, userId);
		
	}
	
	
	@GetMapping("/userTypes")
	public Response<Set<CompanyUserTypesDto>> getCompanyUserType(){
			
			return companyService.getAllCompanyUserTypes();
			
		}
	
	
	@PostMapping("/add")
	public Response<String> companyCreation(@RequestBody CompanyAddDto companyAddDto) {

		return companyService.addCompanyDetails(companyAddDto);

	}
	
	@PostMapping("/branch/add")
	public Response<String> companyBranchCreation(@RequestBody CompanyBranchAddDto companyBranchAddDto) {

		return companyService.addCompanyBranchDetails(companyBranchAddDto);

	}
	
	
	

}
