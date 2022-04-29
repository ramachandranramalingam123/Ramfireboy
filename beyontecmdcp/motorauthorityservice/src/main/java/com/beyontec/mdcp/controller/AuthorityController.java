package com.beyontec.mdcp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.dto.AllAuthorityUsersDto;
import com.beyontec.mdcp.dto.AllUsersDTO;
import com.beyontec.mdcp.dto.AuthorityUserDto;
import com.beyontec.mdcp.dto.CertificateAllocateDto;
import com.beyontec.mdcp.dto.CertificateAuthorityNotifyDto;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.service.AuthorityService;
import com.beyontec.mdcp.service.UserService;

@RestController
@RequestMapping("/authority")
public class AuthorityController {
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private UserService userService;
	
@GetMapping("/notify/certificate")
public Response<List<CertificateAuthorityNotifyDto>> certificateAuthorityNotify(@RequestHeader Integer userId){
		
		return authorityService.authorityNotiifyCertificates(userId);
		
	}

@PutMapping("/notify/reject")
public Response<Integer> certificateAuthorityNotifyReject(@RequestParam Integer allocationId, @RequestHeader Integer userId){
		
		return authorityService.rejectAuthorityNotiifyCertificates(allocationId,userId);
		
	}


@PutMapping("/certificateAllocation")
public Response<String> certificateAllocation(@RequestBody CertificateAllocateDto certificateAllocateDto, @RequestHeader Integer userId){
	
	return authorityService.allocateCertificates(certificateAllocateDto, userId);
	
}

@PostMapping("user/create")
public Response<String> authorityUserCreation(@RequestBody AuthorityUserDto authorityUserDto){
	
	return authorityService.createAuthorityUser(authorityUserDto);
	
}



@GetMapping("/users")
public Response<AllAuthorityUsersDto> getAuthorityAllUsers(@RequestParam("pageSize") Integer pageSize,
		@RequestParam("currentPage") Integer currentPage, @RequestParam("dir") String dir, 
		@RequestParam("prop") String prop, @RequestParam("value") String value) {

	return userService.getAuthorityUsers(pageSize, currentPage, dir, prop, value);

}




}
