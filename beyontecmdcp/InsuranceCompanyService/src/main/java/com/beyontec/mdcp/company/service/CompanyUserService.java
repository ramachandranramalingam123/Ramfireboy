package com.beyontec.mdcp.company.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.beyontec.mdcp.company.dto.CompanyUserDto;
import com.beyontec.mdcp.company.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.company.model.CompanyBranch;
import com.beyontec.mdcp.company.model.CompanyUserBranch;
import com.beyontec.mdcp.company.model.TokenDetail;
import com.beyontec.mdcp.company.model.User;
import com.beyontec.mdcp.company.repo.CompanyBranchRepo;
import com.beyontec.mdcp.company.repo.CompanyUserBranchRepo;
import com.beyontec.mdcp.company.repo.CompanyUserTypeRepo;
import com.beyontec.mdcp.company.repo.TokenRepo;
import com.beyontec.mdcp.company.repo.UserRepo;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.util.DesPasswordEncoder;
import com.beyontec.mdcp.company.util.HandlebarTemplateLoader;
import com.beyontec.mdcp.company.util.InsuranceCompanyConstants;

@Service
public class CompanyUserService {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired 
	private ModelMapper modelMapper;
	
	@Autowired 
	private HandlebarTemplateLoader handlebarTemplateLoader;
	
	@Autowired 
	private SendMailService sendMailService;
	
	@Autowired
	private CompanyUserTypeRepo companyUserTypeRepo;
	
	@Autowired
	private CompanyBranchRepo companyBranchRepo;
	
	@Autowired
	private CompanyUserBranchRepo companyUserBranchRepo;

    @Autowired
	private TokenRepo tokenRepo;

	public Response<String> editOrAddCompanyUser(CompanyUserDto userDto) {

		Response<String> response = new Response<>();

		if (StringUtils.isEmpty(userDto.getUserId())) {
			
			User userData = userRepo.findByUserName(userDto.getUserName());
			if (userData != null) {

				response.setMessage(InsuranceCompanyConstants.USERNAME_UNIQUE);
				response.setStatus(200);
				return response;
			}

			User user = modelMapper.map(userDto, User.class);
			user.setStatus("A");
			user.setCompanyId(userDto.getCompanyId());
			user.setUserAccount("CU");
			user.setCreadtedDate(LocalDateTime.now());
			user.setCreadtedBy(userDto.getCreatedBy());
			user.setDesingnation(userDto.getDesingnation());
			user.setIsPasswordUpdated("Y");
			user.setUserTypeId(userDto.getUserTypeId());
			DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
			user.setPassword(DesPasswordEncoder.encryptPassword(userDto.getPassword()));
			if (!StringUtils.isEmpty(userDto.getSignature())) {
				user.setSignature(Base64.getDecoder().decode(userDto.getSignature()));
			} else {
				user.setSignature(null);
			}
			
			if (!StringUtils.isEmpty(userDto.getPhoto())) {
				user.setPhoto(Base64.getDecoder().decode(userDto.getPhoto()));
			}
			user.setBranchUserType("N");
			user.setMailStatus("N");
			User newUser = userRepo.save(user);
			if(userDto.getCompanyBranchId() != null) {
				CompanyUserBranch  companyUserBranch = null;
				for(Integer companyBranchId : userDto.getCompanyBranchId()) {
					CompanyBranch companyBranchData = companyBranchRepo.findByCompanyBranchId(companyBranchId);
					 companyUserBranch = new CompanyUserBranch();
		             companyUserBranch.setUser(newUser);
		             companyUserBranch.setBranch(companyBranchData.getBranch());
		             companyUserBranch.setCreatedBy(newUser.getUserId());
		             companyUserBranch.setCreatedDate(LocalDateTime.now());
		             if(userDto.getPrimaryCompanyBranchId().equals(companyBranchId)) {
		            	 companyUserBranch.setPrimaryBranch("Y");
		             }else {
		            	 companyUserBranch.setPrimaryBranch("N"); 
		            	
		             }
		             companyUserBranch.setCompanyBranch(companyBranchData);
		            companyUserBranchRepo.save(companyUserBranch);
		            }
				}
			
//			
//			Map<String, Object> data = new HashMap<>();
//			data.put("fullName", userDto.getFirstName() + " " + userDto.getLastName());
//			data.put("userName", user.getUserName());
//			data.put("password", userDto.getPassword());
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
//			sendMailService.sendEmail(mailDto);

			response.setData(newUser.getUserId().toString());
			response.setMessage(InsuranceCompanyConstants.NEW_COMPANY_USER_ADDED);
		} else {

			User user = userRepo.findByUserId(userDto.getUserId());
			

			if (ObjectUtils.isEmpty(user)) {
				throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_USER_NOT_FOUND);
			}

			modelMapper.map(userDto, user);
			
			user.setUpdatedBy(userDto.getUserId());
			user.setUpdatedDate(LocalDateTime.now());
			DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
			user.setPassword(DesPasswordEncoder.encryptPassword(userDto.getPassword()));
			if (!StringUtils.isEmpty(userDto.getSignature())) {
				user.setSignature(Base64.getDecoder().decode(userDto.getSignature()));
			} else {
				user.setSignature(null);
			}
			
			if (!StringUtils.isEmpty(userDto.getPhoto())) {
				user.setPhoto(Base64.getDecoder().decode(userDto.getPhoto()));
			}
			
			user.setUserTypeId(user.getUserTypeId());
			User editUser =userRepo.save(user);
			if(userDto.getCompanyBranchId() != null) {
				CompanyUserBranch  companyUserBranch = null;
				for(Integer companyBranchId : userDto.getCompanyBranchId()) {
					CompanyBranch companyBranchData = companyBranchRepo.findByCompanyBranchId(companyBranchId);
					companyUserBranch = companyUserBranchRepo.findByBranchAndUser(companyBranchData.getBranch(), user);
					if(companyUserBranch == null) {
					 companyUserBranch = new CompanyUserBranch();
					}
		             companyUserBranch.setUser(editUser);
		             companyUserBranch.setBranch(companyBranchData.getBranch());
		             companyUserBranch.setCreatedBy(editUser.getUserId());
		             companyUserBranch.setCreatedDate(LocalDateTime.now());
		             if(userDto.getPrimaryCompanyBranchId().equals(companyBranchId)) {
		            	 companyUserBranch.setPrimaryBranch("Y");
		             }else {
		            	 companyUserBranch.setPrimaryBranch("N"); 
		            	
		             }
		             companyUserBranch.setCompanyBranch(companyBranchData);
		            companyUserBranchRepo.save(companyUserBranch);
		            }
				}
			
			response.setData(user.getUserId().toString());
			response.setMessage(InsuranceCompanyConstants.COMPANY_USER_EDITED);
		}

		response.setStatus(200);
		return response;
	}
	
	public Response<Map<String, Integer>> getUserId(String token) {
		Response<Map<String, Integer>> response = new Response<>();
		TokenDetail userToken = tokenRepo.findByToken(token);
		if (userToken == null) {
			response.setMessage("Invalid token.");
			return response;
		}
		Map<String,Integer> userIdMap = new HashMap<>();
		userIdMap.put("userId", userToken.getUserId());
		response.setData(userIdMap);
		return response;
	}	
}
