package com.beyontec.mdcp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class TestController {
	
	
	
	
	
	
	
	@GetMapping("/test")
	public String testApp() {
		
		return "YES Service WORKING FINE......";
	}
	


}
