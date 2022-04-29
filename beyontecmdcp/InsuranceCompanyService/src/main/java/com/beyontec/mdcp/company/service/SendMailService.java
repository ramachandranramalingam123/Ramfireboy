package com.beyontec.mdcp.company.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.beyontec.mdcp.company.dto.SendMailDto;
import com.beyontec.mdcp.company.response.Response;





@Service
public class SendMailService {

	@Value("${send_mail_url}")
	private String sendMailURL;

	/**
	 * Function for sending mail
	 * 
	 * @param sendMailDto - dto for email, to be send to the user
	 */
	public void sendEmail(SendMailDto mail) throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForObject(sendMailURL, mail, Response.class);
	}

}
