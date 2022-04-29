package com.beyontec.mdcp.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.company.dto.CompanyUserDto;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.service.CompanyUserService;



@RestController
@RequestMapping("/users")
public class CompanyUserController {
	
	@Autowired
	private CompanyUserService userService;
	
	
	@PutMapping("/editUser")
	public Response<String> getUser(@RequestBody CompanyUserDto companyUserDto) {

		return userService.editOrAddCompanyUser(companyUserDto);

	}

}
