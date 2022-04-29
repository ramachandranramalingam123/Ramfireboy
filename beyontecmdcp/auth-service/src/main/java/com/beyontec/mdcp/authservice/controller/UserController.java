package com.beyontec.mdcp.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.authservice.dto.LoginDto;
import com.beyontec.mdcp.authservice.dto.LoginInformation;
import com.beyontec.mdcp.authservice.dto.UserInformation;
import com.beyontec.mdcp.authservice.response.Response;
import com.beyontec.mdcp.authservice.service.LoginService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	LoginService loginService;

	@PostMapping("/signIn")
	public @ResponseBody Response<LoginInformation> login(@Validated @RequestBody LoginDto login) {
		return loginService.authServiceLogin(login);
	}
	
	@PostMapping("/details")
	public @ResponseBody Response<UserInformation> loginDetails(@Validated @RequestBody LoginDto login) {
		return loginService.authServiceLoginDetails(login);
	}

}
