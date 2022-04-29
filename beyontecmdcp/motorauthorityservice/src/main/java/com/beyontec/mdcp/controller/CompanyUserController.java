package com.beyontec.mdcp.controller;

import java.io.IOException;

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
import org.springframework.web.multipart.MultipartFile;

import com.beyontec.mdcp.dto.AllUsersDTO;
import com.beyontec.mdcp.dto.CompanyUserDto;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.service.UserService;

@RestController
@RequestMapping("/users")
public class CompanyUserController {

	@Autowired
	private UserService userService;

	@GetMapping
	public Response<AllUsersDTO> getAllUsers(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("currentPage") Integer currentPage, @RequestParam("dir") String dir, 
			@RequestParam("prop") String prop, @RequestParam("value") String value) {

		return userService.getAllUsers(pageSize, currentPage, dir, prop, value);

	}
	
	@GetMapping("/trafficAuthority")
	public Response<AllUsersDTO> getAllTrafficAuthorityUsers(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("currentPage") Integer currentPage, @RequestParam("dir") String dir, 
			@RequestParam("prop") String prop, @RequestParam("value") String value) {

		return userService.getAllTrafficAuthorityUsers(pageSize, currentPage, dir, prop, value);

	}
	
	@GetMapping("/company")
	public Response<AllUsersDTO> getAllCompanyUsers(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("currentPage") Integer currentPage, @RequestParam Integer companyId, @RequestParam("dir") String dir, 
			@RequestParam("prop") String prop, @RequestParam("value") String value) {

		return userService.getAllCompanyUsers(pageSize, currentPage, companyId, dir, prop, value);

	}
	
	@GetMapping("/details")
	public Response<CompanyUserDto> getUser(@RequestParam("userId") Integer userId) {

		return userService.getComapnyUser(userId);

	}
	
	
	@PutMapping("/editUser")
	public Response<String> getUser(@RequestBody CompanyUserDto companyUserDto) {

		return userService.editOrAddCompanyUser(companyUserDto);

	}
	
	@PutMapping("/trafficAuthority")
	public Response<String> editTrafficAuthority(@RequestBody CompanyUserDto companyUserDto) {

		return userService.editOrAddTrafficAuthorityUser(companyUserDto);

	}
	
	
	@DeleteMapping("/removeUser")
	public Response<String> removeUser(@RequestParam Integer userId, @RequestHeader Integer loginId) {

		return userService.userRemoval(userId, loginId);

	}
	
	@PutMapping("/unlockUser")
	public Response<String> unlockUser(@RequestParam Integer userId, @RequestHeader Integer loginId) {

		return userService.userUnlock(userId, loginId);

	}
	
	@PostMapping("/companyusers/import-excel")
	public Response<String> importCompanyUserExcelFile(@RequestParam("file") MultipartFile files, @RequestParam Integer userId) throws IOException {
		return userService.importCompanyusers(files,userId);
		
		
	}
	
	@PostMapping("/authorityusers/import-excel")
	public Response<String> importAuthorityUserExcelFile(@RequestParam("file") MultipartFile files, @RequestParam Integer userId ) throws IOException {
		return userService.importAuthorityUsers(files,userId);
		
		
	}

}
