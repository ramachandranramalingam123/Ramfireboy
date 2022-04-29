package com.beyontec.mdcp.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.beyontec.mdcp.authservice.response.Response;
import com.beyontec.mdcp.authservice.service.DocumentService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/document")
public class DoocumentController {
	
	@Autowired
	private DocumentService documentService;
	
	@GetMapping(value = "/download/certificate.xlsx")
    public ResponseEntity<InputStreamResource> excelCertificateReport() throws IOException {

     
    
    ByteArrayInputStream in = documentService.customersToExcel();
    // return IOUtils.toByteArray(in);
    
    HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=certificate.xlsx");
    
     return ResponseEntity
                  .ok()
                  .headers(headers)
                  .body(new InputStreamResource(in));
    }
	


	
	@GetMapping(value = "/download/companyuser.xlsx")
    public ResponseEntity<InputStreamResource> excelCompanyUserReport() throws IOException {
     
    
    ByteArrayInputStream in = documentService.companyUserToExcel();
    // return IOUtils.toByteArray(in);
    
    HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=companyuser.xlsx");
    
     return ResponseEntity
                  .ok()
                  .headers(headers)
                  .body(new InputStreamResource(in));
    }

	@GetMapping(value = "/download/authorityuser.xlsx")
    public ResponseEntity<InputStreamResource> excelAuthorityUserReport() throws IOException {
     
    
    ByteArrayInputStream in = documentService.authorityUserToExcel();
    // return IOUtils.toByteArray(in);
    
    HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=AuthorityUser.xlsx");
    
     return ResponseEntity
                  .ok()
                  .headers(headers)
                  .body(new InputStreamResource(in));
    }


}
