package com.beyontec.mdcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.dto.BranchDataDto;
import com.beyontec.mdcp.dto.CompanyBranchDto;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.service.CompanyBranchService;

@RestController
@RequestMapping("/branch")
public class BranchController {
	
	@Autowired
	private CompanyBranchService companyBranchService;
	
	
	@GetMapping("/company/page")
	public Response<BranchDataDto> getCompanyUserBranchPage(@RequestParam("userId") Integer userId,
			@RequestParam(value = "pageSize", required = false ) Integer pageSize,
			@RequestParam(value = "currentPage", required = false) Integer currentPage) {

		return companyBranchService.getCompanyUserBranchDetailsPage(userId,pageSize, currentPage);

	}
	
	@GetMapping("/list")
	public Response<List<CompanyBranchDto>> getCompanyUserBranch(@RequestParam("userId") Integer userId) {

		return companyBranchService.getCompanyUserBranchDetails(userId);

	}



}
