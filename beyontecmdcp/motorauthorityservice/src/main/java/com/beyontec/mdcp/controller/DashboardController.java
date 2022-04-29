package com.beyontec.mdcp.controller;

import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.dto.CertificateDTO;
import com.beyontec.mdcp.dto.InsuranceCompanies;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.service.CompanyService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private CompanyService companyService;

	@GetMapping("/companies")
	public @ResponseBody Response<InsuranceCompanies> getInsuranceCompanies(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("currentPage") Integer currentPage, @RequestParam(value = "companyName", required = false)  String companyName) {

		return companyService.getInsuranceCompanies(pageSize, currentPage, companyName);
	}

	@GetMapping("/certificates")
	public Response<CertificateDTO> getCertificateDetails(@RequestParam("companyId") Integer companyId,
			@RequestParam("pageSize") int pageSize, @RequestParam("currentPage") int currentPage,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) throws ParseException {

		return companyService.getCertificateDetails(companyId, pageSize, currentPage, filter, startDate, endDate);
	}
	

	@GetMapping("/count/totalissuedcerts")
	public @ResponseBody Response<Map<String, Long>> countAllIssuedCertificates() {

		return companyService.countAllIsssuedCertificates();
	}
	

	

}
