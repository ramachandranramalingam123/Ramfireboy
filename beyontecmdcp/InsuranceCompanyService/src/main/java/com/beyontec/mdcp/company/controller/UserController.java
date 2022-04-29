package com.beyontec.mdcp.company.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.service.CompanyUserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private CompanyUserService userService;
	
	@GetMapping("/id")
	public @ResponseBody Response<Map<String, Integer>> login(@RequestParam String token) {
		return userService.getUserId(token);
	}

}
