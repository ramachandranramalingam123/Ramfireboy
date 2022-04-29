package com.beyontec.mdcp.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.authservice.dto.CertificateDetailsDTO;
import com.beyontec.mdcp.authservice.response.Response;
import com.beyontec.mdcp.authservice.service.CertificateService;

@RestController
@RequestMapping("/certificate")
public class CertificateController {
	
	@Autowired
	private CertificateService certificateService;

	@GetMapping("/verify")
	public Response<CertificateDetailsDTO> getCertificateDetails(@RequestParam("certificateNo") String certtificateNo) {

		return certificateService.getCertificateDetails(certtificateNo);
	}
	
}
