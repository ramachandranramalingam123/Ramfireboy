package com.beyontec.mdcp.notification.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.notification.dto.SendMailDto;
import com.beyontec.mdcp.notification.response.Response;
import com.beyontec.mdcp.notification.service.mail.EmailService;


@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping()
public class EmailController {

	@Autowired
	private EmailService emailService;
	
	@PostMapping("/sendMail")
	public Response<String> sendMail(@RequestBody SendMailDto sendMailDto) {
		Response<String> response = new Response<String>();
		
		emailService.sendMail(sendMailDto);
		
		response.setData("Success");
		response.setStatus(HttpServletResponse.SC_OK);
		return response;
	}

}
