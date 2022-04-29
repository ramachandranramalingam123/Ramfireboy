package com.beyontec.mdcp.authservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.authservice.dto.ChangePasswordDto;
import com.beyontec.mdcp.authservice.dto.ForgotPasswordDto;
import com.beyontec.mdcp.authservice.dto.Login;
import com.beyontec.mdcp.authservice.dto.LoginDto;
import com.beyontec.mdcp.authservice.dto.RefTokenResDto;
import com.beyontec.mdcp.authservice.dto.RefreshTokenDto;
import com.beyontec.mdcp.authservice.dto.UserInformation;
import com.beyontec.mdcp.authservice.model.CustomerLoginResponse;
import com.beyontec.mdcp.authservice.response.Response;
import com.beyontec.mdcp.authservice.service.LoginService;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	LoginService loginService;
	
	@PostMapping()
    public ResponseEntity loginser(@RequestBody Login login) {
    	
    	return  loginService.loginWithOauthToken(login);
    
    }
	
	@PostMapping("/signIn")
	public @ResponseBody Response<UserInformation> login(@Validated @RequestBody LoginDto login) {
		return loginService.login(login);
	}
	
	@PostMapping("/company/signIn")
	public @ResponseBody Response<UserInformation> companyLogin(@Validated @RequestBody LoginDto login) {
		return loginService.companyLogin(login);
	}
	
	@GetMapping("/signOut")
	public @ResponseBody Response<UserInformation> signOut(@RequestParam String token) {
		return loginService.signOut(token);
		
	}
	
	@PostMapping("/forgotpassword")
	public @ResponseBody Response<String> forgotPassword(@RequestBody ForgotPasswordDto forgotPassword) {
		return loginService.forgotPassword(forgotPassword);
	}
	
	@PostMapping("/changePassword")
	public @ResponseBody Response<List<String>> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
		return loginService.changePassword(changePasswordDto);
	}
	
	@GetMapping("/getGuestUserDetails")
	public @ResponseBody Response<UserInformation> getGuestUserDetails() {
		return loginService.getGuestUserDetails();
	}
	
	@PostMapping("/refreshToken")
	public @ResponseBody Response<RefTokenResDto> refreshToken(@Validated @RequestBody RefreshTokenDto refreshTokenDto) {
		return loginService.getRefreshToken(refreshTokenDto);
	}


	@PostMapping("/customer/signIn")
	public @ResponseBody Response<CustomerLoginResponse> customerLogin(@Validated @RequestBody LoginDto login) {
		return loginService.customerLogin(login);
	}
	
	@PostMapping("/trafficAuthority/signIn")
	public @ResponseBody Response<UserInformation> trafficAuthorityLogin(@Validated @RequestBody LoginDto login) {
		return loginService.trafficAuthorityLogin(login);
	}
	
	@PutMapping("/unlockUser")
	public Response<String> unlockUser(@RequestParam Integer userId, @RequestHeader Integer loginId) {

		return loginService.userUnlock(userId, loginId);

	}

}
