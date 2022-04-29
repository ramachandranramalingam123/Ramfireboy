package com.beyontec.mdcp.company.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.beyontec.mdcp.company.dto.RevokeCertificateDTO;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.service.DocumentService;
import com.beyontec.mdcp.company.service.FTPService;



@RestController
@RequestMapping("/document")
public class DoocumentController {
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private FTPService ftpService;
	
	@GetMapping("/download/certificateIssue")
    public ResponseEntity<InputStreamResource> excelCustomersReport( @RequestParam Integer companyId,  @RequestParam Integer userTypeId) throws IOException {
    ByteArrayInputStream in = documentService.certificateIssueToExcel(companyId,userTypeId);
    HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=certificateIssueData.xlsx");
    
     return ResponseEntity
                  .ok()
                  .headers(headers)
                  .body(new InputStreamResource(in));
    }
	
	@PostMapping("/certificateIssue/import-excel")
	public Response<String> importExcelFile(@RequestParam("file") MultipartFile files, @RequestParam Integer companyId,
			@RequestParam Integer userId, @RequestParam(value = "branchId", required = false) Integer branchId) throws IOException, ParseException, EncryptedDocumentException, InvalidFormatException {
		return documentService.importIssueCertificate(files,companyId, userId,  branchId);
		
		
	}
	
	@GetMapping("/search/report/certificateIssue")
    public ResponseEntity<InputStreamResource> excelSearchCertificateReport( @RequestParam("pageSize") Integer pageSize,
			@RequestParam("currentPage") Integer currentPage,@RequestParam Integer companyId,
			@RequestParam(value="status", required = false) Integer status,
			@RequestParam(value="issuedBy", required = false) String issuedBy,
			@RequestParam(value="uplodedBy", required = false) Integer uplodedBy,
			@RequestParam(value="startDate", required = false) String startDate,
			@RequestParam(value="endDate", required = false) String endDate,
			@RequestParam(value = "branchId", required = false) List<Integer> branchIds,
			@RequestParam(value = "transactionDate", required = false) String transactionDate,
			@RequestParam(value="reportType", required = false) String reportType) throws ParseException,IOException {
		
    ByteArrayInputStream in = documentService.excelSearchCertificateReport(pageSize, currentPage,companyId, status, 
    		issuedBy,uplodedBy, startDate,endDate, branchIds, transactionDate );

    HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=SearchCertificateReport.xlsx");
    if ("pdf".equalsIgnoreCase(reportType)) {
    	headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=SearchCertificateReport.pdf");
    }
     return ResponseEntity
                  .ok()
                  .headers(headers)
                  .body(new InputStreamResource(in));
    }
	
	
	
	@GetMapping(value = "/download/certificate/image")
	public Response<String> certificateImage(@RequestParam String certificateNo) throws IOException, ParseException {

		return documentService.certificateImageExport(certificateNo);

	}
	
	@GetMapping(value = "/download/certificate/pdf")
	public ResponseEntity<InputStreamResource> certificatePdf(@RequestParam String certificateNo)
			throws IOException, ParseException {

		ByteArrayInputStream in = documentService.certificatePDF(certificateNo);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=certificate.pdf");

		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));

	}
	
	@GetMapping(value = "/show/certificate/image")
	public Response<String> showCertificateImage(@RequestParam String certificateNo) throws IOException, ParseException {

		return documentService.certificateImageShow(certificateNo);

	}
	
	@GetMapping(value = "/revoke/certificate/image")
	public Response<RevokeCertificateDTO> revokeCertificateImage(@RequestParam String certificateNo)
			throws IOException, ParseException {

		return documentService.getRevokeCertificate(certificateNo);

	}
	
	@GetMapping("/sendmail")
	public Response<String> mailCertificate(@RequestParam String certificateNo, @RequestParam String email) {
		
		return documentService.sendMail(certificateNo, email);
	}
	
	@GetMapping("/download/payment")
    public ResponseEntity<InputStreamResource> paymentDetails(@RequestParam String fileName) throws IOException {

		ByteArrayInputStream in = ftpService.downloadFTPFile(fileName);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename="+fileName);

		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
	}
}
