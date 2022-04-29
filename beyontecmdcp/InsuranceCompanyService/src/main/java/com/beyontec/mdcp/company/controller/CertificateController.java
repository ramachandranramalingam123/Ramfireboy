package com.beyontec.mdcp.company.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.beyontec.mdcp.company.dto.CertificateAllocationReqDto;
import com.beyontec.mdcp.company.dto.CertificateDTO;
import com.beyontec.mdcp.company.dto.CertificateDetailsDTO;
import com.beyontec.mdcp.company.dto.CertificateImageDTO;
import com.beyontec.mdcp.company.dto.CertificateIssueDto;
import com.beyontec.mdcp.company.dto.CertificateRevokeDto;
import com.beyontec.mdcp.company.dto.InternalAllocationDataDto;
import com.beyontec.mdcp.company.dto.InternalAllocationDto;
import com.beyontec.mdcp.company.dto.RevokedReasonsDto;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.service.CertificateService;

@RestController
@RequestMapping("/certificate")
public class CertificateController {

	@Autowired
	private CertificateService certificateService;

	@PostMapping("/internalAllocation")

	public Response<String> internalAllocation(@RequestBody InternalAllocationDataDto internalAllocationDataDto) {
		return certificateService.addinternalAllocation(internalAllocationDataDto);

	}

	@PostMapping("/singleAllocation")
	public Response<String> singleAllocation(@RequestBody InternalAllocationDto internalAllocationDto) {
		return certificateService.addSingleAllocation(internalAllocationDto);

	}

	@PostMapping("/request")
	public Response<String> requestForNewCertificates(
			@RequestParam Integer companyId,@RequestParam Long certificateReqCount,
			@RequestHeader Integer userId, @RequestParam(value="file",  required = false) MultipartFile paymentDoc, 
			@RequestParam(value="fileType",  required = false) String fileType,
			@RequestParam(value="paymentDescription",  required = false) String paymentDescription) throws IOException {
		
		CertificateAllocationReqDto certificateAllocationReqDto = new CertificateAllocationReqDto();
		certificateAllocationReqDto.setCertificateReqCount(certificateReqCount);
		certificateAllocationReqDto.setCompanyId(companyId);
		return certificateService.createCertificateReq(certificateAllocationReqDto, userId,paymentDoc, fileType, paymentDescription);
	}

	@GetMapping("/details")
	public Response<CertificateDetailsDTO> getCertificateDetails(@RequestParam("certificateNo") String certtificateNo) {

		return certificateService.getCertificateDetails(certtificateNo);
	}

	@GetMapping("/vehicle/details")
	public Response<CertificateDetailsDTO> getCertificateDetailsByVehicle(@RequestParam("vehicleNo") String vehicleNo) {

		return certificateService.getCertificateDetailsByVehicleNo(vehicleNo);
	}

	@PostMapping("/certificateIssue")
	public Response<CertificateImageDTO> certificateIssue(@RequestBody CertificateIssueDto certificateIssueDto)
			throws ParseException {

		return certificateService.newCertificateIssue(certificateIssueDto);
	}

	@PutMapping("/revoke")
	public Response<String> certificateRevoke(@RequestBody CertificateRevokeDto certificateRevokeDto)
			throws ParseException {

		return certificateService.revokeCertificate(certificateRevokeDto);
	}

	@GetMapping("/revoke/reasons")
	public Response<List<RevokedReasonsDto>> certificateRevokeReasons() {

		return certificateService.revokedReasons();
	}

	@GetMapping("/certificateNumber")
	public Response<String> certificateNumAutoSelect(@RequestParam Integer companyId) {

		return certificateService.getCertificateNumber(companyId);
	}

	@RequestMapping(value = "/verify/details", method = RequestMethod.OPTIONS)
	public Response<CertificateDetailsDTO> getCertificateDetailsNoAuth(
			@RequestParam("certificateNo") String certtificateNo) {

		return certificateService.getCertificateDetails(certtificateNo);
	}


	@GetMapping("/search/issuedcerts")
	public Response<CertificateDTO> serachIssuedCertificates(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("currentPage") Integer currentPage, @RequestParam String value,
			@RequestParam Integer companyId, @RequestParam String issuedBy, 
			@RequestParam(value = "branchId", required = false) Integer branchId) {

		return certificateService.searchIssuedCertificates(value, pageSize, currentPage, companyId, issuedBy, branchId);

	}
	
	@GetMapping("/search/reports")
	public Response<CertificateDTO> serachIssuedCertificatesReports(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("currentPage") Integer currentPage,@RequestParam Integer companyId, @RequestParam(value="status", required = false) Integer status,
			@RequestParam(value ="issuedBy", required = false) String issuedBy,@RequestParam(value ="uplodedBy", required = false) Integer uplodedBy,
			@RequestParam(value="startDate", required = false) String startDate,
			@RequestParam(value="endDate", required = false) String endDate,
			@RequestParam(value = "branchId", required = false) List<Integer> branchId,
			@RequestParam(value = "transactionDate", required = false) String transactionDate) throws ParseException {

		return certificateService.searchIssuedCertificatesReports(pageSize, currentPage,companyId, status, 
				issuedBy,uplodedBy, startDate,endDate, branchId, transactionDate );

	}

	@GetMapping("/certificateDetails")
	public Response<CertificateDetailsDTO> getCertificateDetailsByRegistrationNum(
			@RequestParam("registrationNo") String registrationNo) {

		return certificateService.getCertificateDetailsByRegistrationNo(registrationNo);
	}
}
