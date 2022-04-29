package com.beyontec.mdcp.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.authservice.model.SignUp;
import com.beyontec.mdcp.authservice.response.Response;
import com.beyontec.mdcp.authservice.service.EndCustomerService;

@RestController
@RequestMapping("/customer")
public class EndCustomerController {
	
	@Autowired
	private EndCustomerService endCustomerService;

	@PostMapping("/signUp")
	public Response<String> customerSignUp(@RequestBody SignUp signUp) {

		return endCustomerService.createCustomer(signUp);

	}

}
