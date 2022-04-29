package com.beyontec.mdcp.authservice.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.beyontec.mdcp.authservice.dto.CertificateDetailsDTO;
import com.beyontec.mdcp.authservice.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.authservice.model.CertificateDetails;
import com.beyontec.mdcp.authservice.model.CompanyUserType;
import com.beyontec.mdcp.authservice.model.User;
import com.beyontec.mdcp.authservice.repository.CertificateRepo;
import com.beyontec.mdcp.authservice.repository.CompanyUserTypeRepo;
import com.beyontec.mdcp.authservice.repository.UserRepository;
import com.beyontec.mdcp.authservice.response.Response;
import com.beyontec.mdcp.authservice.util.AuthConstants;
import com.beyontec.mdcp.authservice.util.QRCodeGenerator;
import com.google.zxing.WriterException;

@Service
public class CertificateService {
	
	@Autowired
	private CertificateRepo certificateRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CompanyUserTypeRepo companyUserTypeRepo;
	
	public Response<CertificateDetailsDTO> getCertificateDetails(String certificateNo) {

		Response<CertificateDetailsDTO> response = new Response<>();
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateNo);
		
		if(ObjectUtils.isEmpty(certificateDetails)) {
			throw new BadDataExceptionHandler(AuthConstants.INVALID_CERT_NO);
		}

		CertificateDetailsDTO certificateDetailsDTO = modelMapper.map(certificateDetails, CertificateDetailsDTO.class);
		certificateDetailsDTO.setQrCode("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));		
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
		certificateDetailsDTO.setCommencingDate(formatter.format(certificateDetails.getCommencingDate()));
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("hh:mm a");
		certificateDetailsDTO.setCommencingTime(formatter1.format(certificateDetails.getCommencingDate()));

		CompanyUserType companyUserType = companyUserTypeRepo.findByUserTypeId(user.getUserTypeId());
		System.out.println(companyUserType.getUserType()+".........");
		certificateDetailsDTO.setIssuedBy(companyUserType.getUserType());
		response.setData(certificateDetailsDTO);
		response.setStatus(200);
		return response;
	}

	public String getQrBase(String certtificateNo) {
		String qrBase64 = null;


		try {
			qrBase64 = QRCodeGenerator.getQRCodeImage(certtificateNo, 350, 350);
		} catch (WriterException e) {
			System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
		}
		return qrBase64;
	}

}
