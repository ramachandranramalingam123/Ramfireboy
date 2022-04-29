package com.beyontec.mdcp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.beyontec.mdcp.dto.AuthorityUserDto;
import com.beyontec.mdcp.dto.CertificateAllocateDto;
import com.beyontec.mdcp.dto.CertificateAuthorityNotifyDto;
import com.beyontec.mdcp.dto.SendMailDto;
import com.beyontec.mdcp.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.model.CertificateAllocation;
import com.beyontec.mdcp.model.InsuranceCompany;
import com.beyontec.mdcp.model.Roles;
import com.beyontec.mdcp.model.RolesModule;
import com.beyontec.mdcp.model.User;
import com.beyontec.mdcp.repo.CertificateAllocationRepo;
import com.beyontec.mdcp.repo.CompanyRepo;
import com.beyontec.mdcp.repo.RolesModulesRepo;
import com.beyontec.mdcp.repo.RolesRepo;
import com.beyontec.mdcp.repo.UserRepo;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.util.DesPasswordEncoder;
import com.beyontec.mdcp.util.HandlebarTemplateLoader;
import com.beyontec.mdcp.util.MotorAuthorityConstants;
import com.github.jknack.handlebars.Template;

@Service
public class AuthorityService {
	
	@Autowired
	private CertificateAllocationRepo certificateAllocationRepo;
	
	@Autowired
	private CompanyRepo companyRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired 
	private ModelMapper modelMapper;
	
	@Autowired 
	private HandlebarTemplateLoader handlebarTemplateLoader;
	
	@Autowired 
	private SendMailService sendMailService;
	
	@Autowired
	private RolesModulesRepo rolesModulersRepo;
	
	@Autowired
	private RolesRepo rolesRepo;
	
	public Response<List<CertificateAuthorityNotifyDto>> authorityNotiifyCertificates(Integer userId) {
		Response<List<CertificateAuthorityNotifyDto>> response = new Response<List<CertificateAuthorityNotifyDto>>();
		List<CertificateAuthorityNotifyDto> certificateAuthorityDtoList = new ArrayList<>();
		CertificateAuthorityNotifyDto certificateAuthorityNotifyDto = null;
		List<CertificateAllocation> certificateAllocationList = certificateAllocationRepo.findByShowAuthorityNonify(1);
		if(certificateAllocationList != null &&  0 < certificateAllocationList.size()) {
			
			List<InsuranceCompany> insuranceCompaniesList = companyRepo.findAllByStatusOrderByCreatedDateDesc("A");
			
			for(int i=0; i<certificateAllocationList.size(); i++) {
				
				certificateAuthorityNotifyDto = new CertificateAuthorityNotifyDto();
				certificateAuthorityNotifyDto.setCompanyId(certificateAllocationList.get(i).getCompany().getCompanyId());
				certificateAuthorityNotifyDto.setCompanyName(certificateAllocationList.get(i).getCompany().getCompanyName());
				certificateAuthorityNotifyDto.setTotalCertificate(certificateAllocationList.get(i).getRequestedCertificates());
				certificateAuthorityNotifyDto.setAllocationId(certificateAllocationList.get(i).getAllocationId());
				if(certificateAllocationList.get(i).getFileName() != null && certificateAllocationList.get(i).getPaymentFileType() != null) {
					certificateAuthorityNotifyDto.setPaymentFileName(certificateAllocationList.get(i).getFileName()+"."+certificateAllocationList.get(i).getPaymentFileType());
					certificateAuthorityNotifyDto.setPaymentDescription(certificateAllocationList.get(i).getPaymentDescription());
				}
				int val = i;
				OptionalInt index = IntStream.range(0, insuranceCompaniesList.size())
					     .filter(d -> insuranceCompaniesList.get(d).getCompanyId().equals(certificateAllocationList.get(val).getCompany().getCompanyId()))
					     .findFirst();
				if(index.isPresent()) {
					int dataIndex = index.getAsInt()+1;
				        if(dataIndex%15==0) {
				        	certificateAuthorityNotifyDto.setCompanyPage(index.getAsInt()/15);
				        }else if(dataIndex/15>0){
				        	certificateAuthorityNotifyDto.setCompanyPage(index.getAsInt()/15+1);
				        }else {
				        	certificateAuthorityNotifyDto.setCompanyPage(1);
				        }
				}
				
				certificateAuthorityDtoList.add(certificateAuthorityNotifyDto);
			}

			  response.setStatus(HttpStatus.OK.value());
			  response.setMessage(MotorAuthorityConstants.AUTHORITY_CERTIFICATE_REQ_NOTIFY);
			 response.setData(certificateAuthorityDtoList);
		}else {

			  response.setStatus(HttpStatus.OK.value());
			  response.setMessage(MotorAuthorityConstants.NO_NOTIFICATIONS);
		}
		return response;
	}

	public Response<String> allocateCertificates(CertificateAllocateDto certificateAllocateDto, Integer userId) {
		Response<String> response = new  Response<String>();
		InsuranceCompany companyData = companyRepo.findByCompanyId(certificateAllocateDto.getCompanyId());
		if(companyData == null)
			throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_NOT_FOUND);
		
		CertificateAllocation certificateAllocation = null;
		if(certificateAllocateDto.getAllocationId() != null) {
		 certificateAllocation = certificateAllocationRepo.findByAllocationId(certificateAllocateDto.getAllocationId());
		 if(certificateAllocation == null) {
				response.setStatus(HttpStatus.OK.value());
				response.setMessage(MotorAuthorityConstants.INVALID_ALLOCATION_ID);
				return response;
			}
		}else {
			certificateAllocation = new CertificateAllocation();
		}
		
		certificateAllocation.setAllocatedBy(userId);
		certificateAllocation.setAllocatedDate(LocalDateTime.now());
		certificateAllocation.setShowAuthorityNonify(0);
		certificateAllocation.setShowCompanyNonify(1);
		certificateAllocation.setAllocatedCertificates(certificateAllocateDto.getNumOfCertificate());
		certificateAllocation.setIsrejected(0);
		certificateAllocation.setCuIsrejected(0);
		certificateAllocation.setCompany(companyData);
		certificateAllocationRepo.save(certificateAllocation);
		
		Map<String, Object> data = new HashMap<>();
		data.put("count",certificateAllocateDto.getNumOfCertificate());
		data.put("companyName", companyData.getCompanyName());
		
		List<String> mailList = new ArrayList<>();
		RolesModule rolesModule = rolesModulersRepo.findByPortalAndModuleLabel("IC", "Receive Mail");
		
		List<Roles> roles = rolesRepo.findByRolesModule(rolesModule);
		for (Roles role : roles) {
			List<User> users = userRepo.findByRoleIdAndCompanyId(role.getRolesMaster().getMasterId(),
					companyData.getCompanyId());

			users.forEach(user -> {
				mailList.add(user.getEmail());
			});
		}

		if (!mailList.isEmpty()) {
			String htmlContent = null;
			try {
				Template template = handlebarTemplateLoader.getTemplate("certificateAllocation");
				htmlContent = template.apply(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			SendMailDto mailDto = new SendMailDto();
			mailDto.setSubject("MDCP - Certificate allocation");
			mailDto.setMessage(htmlContent);
			mailDto.setToEmail(mailList);
			mailDto.setDisplayEmailSignature(true);
			try {
				sendMailService.sendEmail(mailDto);
			} catch (Exception e) {
				response.setStatus(HttpStatus.OK.value());
				response.setData(companyData.getCompanyName());
				response.setMessage(MotorAuthorityConstants.SUCCESSFULLY_CERTIFICATE_ALLOCATED 
						+ " "+MotorAuthorityConstants.MAIL_QUOTA_EXCEED);
				return response;
			}
		}
		
		response.setStatus(HttpStatus.OK.value());
		response.setData(companyData.getCompanyName());
		response.setMessage(MotorAuthorityConstants.SUCCESSFULLY_CERTIFICATE_ALLOCATED);
		return response;
	}

	public Response<String> createAuthorityUser(AuthorityUserDto authorityUserDto) {
		Response<String> response = new Response<>();
		
		if (StringUtils.isEmpty(authorityUserDto.getUserId())) {
			
			User userData = userRepo.findByUserName(authorityUserDto.getUserName());
			if (userData != null) {

				response.setMessage(MotorAuthorityConstants.USERNAME_UNIQUE);
				response.setStatus(200);
				return response;
			}
			User user = modelMapper.map(authorityUserDto, User.class);
			user.setStatus("A");
			user.setUserAccount("AU");
			user.setCreadtedDate(LocalDateTime.now());
			user.setCreadtedBy(authorityUserDto.getCreatedBy());
			user.setIsPasswordUpdated("Y");
			DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
			user.setPassword(DesPasswordEncoder.encryptPassword(authorityUserDto.getPassword()));
			user.setMailStatus("N");
			if (!StringUtils.isEmpty(authorityUserDto.getPhoto())) {
				user.setPhoto(Base64.getDecoder().decode(authorityUserDto.getPhoto()));
			}
			User newUser = userRepo.save(user);
//			Map<String, Object> data = new HashMap<>();
//			data.put("fullName", authorityUserDto.getFirstName() + " " + authorityUserDto.getLastName());
//			data.put("userName", user.getUserName());
//			data.put("password", authorityUserDto.getPassword());
//			data.put("isInlineImageVisible", "block");
//
//			String htmlContent = null;
//			try {
//				Template template = handlebarTemplateLoader.getTemplate("newuser");
//				htmlContent = template.apply(data);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			SendMailDto mailDto = new SendMailDto();
//			mailDto.setSubject("New User Credentials");
//			mailDto.setMessage(htmlContent);
//			mailDto.setToEmail(Arrays.asList(user.getEmail()));
//			mailDto.setDisplayEmailSignature(true);
		//	sendMailService.sendEmail(mailDto);

			response.setData(newUser.getUserId().toString());
			response.setMessage(MotorAuthorityConstants.NEW_AUTHORITY_USER_ADDED);
		} else {

			User user = userRepo.findByUserId(authorityUserDto.getUserId());

			if (ObjectUtils.isEmpty(user)) {
				throw new BadDataExceptionHandler(MotorAuthorityConstants.AUTHORITY_USER_NOT_FOUND);
			}

			modelMapper.map(authorityUserDto, user);
			user.setUpdatedBy(authorityUserDto.getUserId());
			user.setUpdatedDate(LocalDateTime.now());
			DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
			user.setPassword(DesPasswordEncoder.encryptPassword(authorityUserDto.getPassword()));
			if (!StringUtils.isEmpty(authorityUserDto.getPhoto())) {
				user.setPhoto(Base64.getDecoder().decode(authorityUserDto.getPhoto()));
			} else {
				user.setPhoto(null);
			}
			
			userRepo.save(user);
			response.setData(user.getUserId().toString());
			response.setMessage(MotorAuthorityConstants.AUTHORITY_USER_EDITED);
		}

		response.setStatus(200);
		return response;
	}
public Response<Integer> rejectAuthorityNotiifyCertificates(Integer allocationId, Integer userId) {
		
		Response<Integer> response = new Response();
		CertificateAllocation certificateAllocation = certificateAllocationRepo.findByAllocationIdAndShowAuthorityNonify(allocationId,1);
		if(certificateAllocation == null) {
			response.setMessage(MotorAuthorityConstants.INVALID_ALLOCATION_ID);
			response.setStatus(200);
		} else {
			certificateAllocation.setIsrejected(1);
			certificateAllocation.setRejectedAt(LocalDateTime.now());
			certificateAllocation.setCuRejectedBy(userId);
			certificateAllocation.setShowAuthorityNonify(0);
			certificateAllocationRepo.save(certificateAllocation);

			User requstedUser = userRepo.findByUserId(certificateAllocation.getRequestedBy());

			Map<String, Object> data = new HashMap<>();
			data.put("count", certificateAllocation.getRequestedCertificates());
			
			InsuranceCompany companyData = companyRepo.findByCompanyId(requstedUser.getCompanyId());
			if(companyData == null)
				throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_NOT_FOUND);
			
			data.put("companyName", companyData.getCompanyName());

			String htmlContent = null;
			try {
				Template template = handlebarTemplateLoader.getTemplate("rejectCertificateAllocation");
				htmlContent = template.apply(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			SendMailDto mailDto = new SendMailDto();
			mailDto.setSubject("MDCP - Certificate rejection by ASSAR");
			mailDto.setMessage(htmlContent);
			mailDto.setToEmail(Arrays.asList(requstedUser.getEmail()));
			mailDto.setDisplayEmailSignature(true);
			try {
				sendMailService.sendEmail(mailDto);
			} catch (Exception e) {
				response.setStatus(HttpStatus.OK.value());
				response.setMessage(MotorAuthorityConstants.AUTHORITY_NOTIFICATION_REJECTED 
						+ " "+MotorAuthorityConstants.MAIL_QUOTA_EXCEED);
				response.setStatus(200);
				return response;
			}

			response.setMessage(MotorAuthorityConstants.AUTHORITY_NOTIFICATION_REJECTED);
			response.setStatus(200);
		}
		return response;
	}
}
