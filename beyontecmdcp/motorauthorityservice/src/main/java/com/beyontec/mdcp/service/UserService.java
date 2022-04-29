package com.beyontec.mdcp.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.beyontec.mdcp.dto.AllAuthorityUsersDto;
import com.beyontec.mdcp.dto.AllUsersDTO;
import com.beyontec.mdcp.dto.CompanyUserDto;
import com.beyontec.mdcp.dto.UsersAuthorityDto;
import com.beyontec.mdcp.dto.UsersDto;
import com.beyontec.mdcp.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.model.CompanyBranch;
import com.beyontec.mdcp.model.CompanyUserBranch;
import com.beyontec.mdcp.model.CompanyUserType;
import com.beyontec.mdcp.model.InsuranceCompany;
import com.beyontec.mdcp.model.RolesMaster;
import com.beyontec.mdcp.model.User;
import com.beyontec.mdcp.repo.CompanyBranchRepo;
import com.beyontec.mdcp.repo.CompanyRepo;
import com.beyontec.mdcp.repo.CompanyUserBranchRepo;
import com.beyontec.mdcp.repo.CompanyUserTypeRepo;
import com.beyontec.mdcp.repo.RolesMasterRepo;
import com.beyontec.mdcp.repo.UserRepo;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.util.DesPasswordEncoder;
import com.beyontec.mdcp.util.GenerationUtils;
import com.beyontec.mdcp.util.HandlebarTemplateLoader;
import com.beyontec.mdcp.util.MotorAuthorityConstants;


@Service
public class UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private CompanyRepo companyRepo;
	
	@Autowired
	private GenerationUtils generationUtils;

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
	private RolesMasterRepo rolesMasterRepo;

    @PersistenceContext
    private EntityManager entityManager;
    
	public Response<AllUsersDTO> getAllUsers(int pageSize, int currentPage, String dir, String prop, String value) {

		Response<AllUsersDTO> response = new Response<>();
		AllUsersDTO allUsersDTO = new AllUsersDTO();
		
		String sortingStr = "";
		if(!StringUtils.isEmpty(value)) {
			sortingStr = "AND (";
			List<InsuranceCompany> compList = companyRepo.findByCompanyNameLike(value);
			if (CollectionUtils.isNotEmpty(compList)) {
				List<Integer> ids = compList.stream()
                        .map(InsuranceCompany::getCompanyId).collect(Collectors.toList());
				sortingStr += "user.companyId in ("+org.apache.commons.lang.StringUtils.join(ids, ",") + " ) OR ";
			}
			sortingStr += "user.userName like '%"+value+"%' OR user.firstName like '%"+value+"%') ";
			String status = null;
			if (org.apache.commons.lang.StringUtils.containsIgnoreCase("Active", value)) {
				status = "A";
			} else if (org.apache.commons.lang.StringUtils.containsIgnoreCase("Locked", value)) {
				status = "AL";
			} else if (org.apache.commons.lang.StringUtils.containsIgnoreCase("Inactive", value)) {
				status = "I";
			}
			if (status != null) {
				sortingStr = "AND user.status like '%" + status + "%' ";
			}
		}
		if (!StringUtils.isEmpty(prop)) {
			sortingStr += "order by ";
			if ("userName".equalsIgnoreCase(prop))
				sortingStr += "user.userName ";
			else if ("firstName".equalsIgnoreCase(prop))
				sortingStr += "user.firstName ";
			else if ("companyName".equalsIgnoreCase(prop))
				sortingStr += "user.insureCompany ";
			else if ("status".equalsIgnoreCase(prop))
				sortingStr += "user.status ";
			sortingStr += dir;
		}
		String query = "Select user from User user where user.userAccount =\'CU\' " + sortingStr;
		List<User> userList = entityManager
				.createQuery(query, User.class)
				.setFirstResult(currentPage * pageSize)
				.setMaxResults(pageSize).getResultList();

		List<UsersDto> listDto = new ArrayList<UsersDto>();

		for (User user : userList) {

			UsersDto userDto = new UsersDto();
			userDto.setUserId(user.getUserId());
			userDto.setUserName(user.getUserName());
			userDto.setFirstName(user.getFirstName());
			userDto.setCompanyId(user.getCompanyId());
			if("A".equals(user.getStatus())) {
			userDto.setStatus("Active");
			}else if("AL".equals(user.getStatus())) {
			userDto.setStatus("Locked");
			}else if("I".equals(user.getStatus())) {
			userDto.setStatus("Inactive");
			}
			InsuranceCompany company = companyRepo.findByCompanyId(user.getCompanyId());

			if (!ObjectUtils.isEmpty(company)) {
				userDto.setCompanyName(company.getCompanyName());
			}

			listDto.add(userDto);
		}

		allUsersDTO.setUsersDto(listDto);

		allUsersDTO.setTotalCount(entityManager
				.createQuery(query, User.class)
				.getResultList().size());
		response.setData(allUsersDTO);
		response.setStatus(200);

		return response;
	}
	
	public Response<AllUsersDTO> getAllCompanyUsers(int pageSize, int currentPage, int companyId, String dir, String prop, String value) {

		Response<AllUsersDTO> response = new Response<>();
		String sortingStr = "";
		if(!StringUtils.isEmpty(value)) {
			sortingStr = "AND (";
			List<InsuranceCompany> compList = companyRepo.findByCompanyNameLike(value);
			if (CollectionUtils.isNotEmpty(compList)) {
				List<Integer> ids = compList.stream()
                        .map(InsuranceCompany::getCompanyId).collect(Collectors.toList());
				sortingStr += "user.companyId in ("+org.apache.commons.lang.StringUtils.join(ids, ",") + " ) OR ";
			}
			sortingStr += "user.userName like '%"+value+"%' OR user.firstName like '%"+value+"%') ";
			String status = null;
			if (org.apache.commons.lang.StringUtils.containsIgnoreCase("Active", value)) {
				status = "A";
			} else if (org.apache.commons.lang.StringUtils.containsIgnoreCase("Locked", value)) {
				status = "AL";
			} else if (org.apache.commons.lang.StringUtils.containsIgnoreCase("Inactive", value)) {
				status = "I";
			}
			if (status != null) {
				sortingStr += "or user.status = '" + status + "' ";
			}
		}
		if (!StringUtils.isEmpty(prop)) {
			sortingStr += "order by ";
			if ("userName".equalsIgnoreCase(prop))
				sortingStr += "user.userName ";
			else if ("firstName".equalsIgnoreCase(prop))
				sortingStr += "user.firstName ";
			else if ("companyName".equalsIgnoreCase(prop))
				sortingStr += "user.insureCompany ";
			else if ("status".equalsIgnoreCase(prop))
				sortingStr += "user.status ";
			sortingStr += dir;
		}
		String query = "Select user from User user where user.userAccount =\'CU\' and user.companyId= "+ companyId + sortingStr;
		List<User> users = entityManager
				.createQuery(query, User.class)
				.setFirstResult(currentPage * pageSize)
				.setMaxResults(pageSize).getResultList();
		
		AllUsersDTO allUsersDTO = new AllUsersDTO();
		List<UsersDto> listDto = new ArrayList<UsersDto>();

		for (User user : users) {

			UsersDto userDto = new UsersDto();
			userDto.setUserId(user.getUserId());
			userDto.setUserName(user.getUserName());
			userDto.setCompanyId(user.getCompanyId());
			userDto.setFirstName(user.getFirstName());
			if ("A".equals(user.getStatus())) {
				userDto.setStatus("Active");
			} else if ("AL".equals(user.getStatus())) {
				userDto.setStatus("Locked");
			} else if ("I".equals(user.getStatus())) {
				userDto.setStatus("Inactive");
			}
			InsuranceCompany company = companyRepo.findByCompanyId(user.getCompanyId());

			if (!ObjectUtils.isEmpty(company)) {
				userDto.setCompanyName(company.getCompanyName());
			}

			listDto.add(userDto);
		}

		allUsersDTO.setUsersDto(listDto);

		allUsersDTO.setTotalCount(entityManager
				.createQuery(query, User.class).getResultList().size());
		response.setData(allUsersDTO);
		response.setStatus(200);

		return response;
	}



	public Response<CompanyUserDto> getComapnyUser(int userId) {

		Response<CompanyUserDto> response = new Response<>();
		User user = userRepo.findByUserId(userId);

		if (ObjectUtils.isEmpty(user)) {
			throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_USER_NOT_FOUND);
		}

		CompanyUserDto userDto = modelMapper.map(user, CompanyUserDto.class);
		
		List<CompanyUserBranch> companyUserBranch = companyUserBranchRepo.findByUser(user);
		List<Integer> companyBranchId = new ArrayList<>();
 		for(CompanyUserBranch companyUserBranchData : companyUserBranch) {
 			companyBranchId.add(companyUserBranchData.getCompanyBranch().getCompanyBranchId());
 			if("Y".equals(companyUserBranchData.getPrimaryBranch())) {
 				userDto.setPrimaryCompanyBranchId(companyUserBranchData.getCompanyBranch().getCompanyBranchId());
 			}
 			
		}
 		userDto.setCompanyBranchId(companyBranchId);
		DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
		userDto.setPassword(DesPasswordEncoder.decryptPassword(user.getPassword()));
		if (!StringUtils.isEmpty(user.getSignature())) {
			userDto.setSignature("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getSignature()));
		}
		if (!StringUtils.isEmpty(user.getPhoto())) {
			userDto.setPhoto("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getPhoto()));
		}
		response.setData(userDto);
		response.setStatus(200);

		return response;
	}
	
	
	public Response<String> editOrAddCompanyUser(CompanyUserDto userDto) {

		Response<String> response = new Response<>();

		if (StringUtils.isEmpty(userDto.getUserId())) {
			
			User userData = userRepo.findByUserName(userDto.getUserName());
			if (userData != null) {

				response.setMessage(MotorAuthorityConstants.USERNAME_UNIQUE);
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
			CompanyUserType  companyUserType =companyUserTypeRepo.findByUserTypeId(userDto.getUserTypeId());
			user.setUserTypeId(companyUserType.getUserTypeId());
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
			user.setBranchUserType("A");
			user.setMailStatus("N");
			User newUser = userRepo.save(user);
			InsuranceCompany company = companyRepo.findByCompanyId(userDto.getCompanyId());
			List<CompanyBranch> companyBranch = companyBranchRepo.findByCompany(company);
			if(companyBranch != null) {
			CompanyUserBranch  companyUserBranch = null;
            for(CompanyBranch companyBranchData : companyBranch) {
             companyUserBranch = new CompanyUserBranch();
             companyUserBranch.setUser(newUser);
             companyUserBranch.setBranch(companyBranchData.getBranch());
             companyUserBranch.setCreatedBy(newUser.getUserId());
             companyUserBranch.setCreatedDate(LocalDateTime.now());
             if(companyBranch.get(0).getBranch().equals(companyBranchData.getBranch())) {
            	 companyUserBranch.setPrimaryBranch("Y");
            }else {
            	companyUserBranch.setPrimaryBranch("N");
            }
             companyUserBranch.setCompanyBranch(companyBranchData);
            companyUserBranchRepo.save(companyUserBranch);
            }
			}
		
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
			response.setMessage(MotorAuthorityConstants.NEW_COMPANY_USER_ADDED);
		} else {

			User user = userRepo.findByUserId(userDto.getUserId());

			if (ObjectUtils.isEmpty(user)) {
				throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_USER_NOT_FOUND);
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
			
			CompanyUserType companyUserType = companyUserTypeRepo.findByUserTypeId(userDto.getUserTypeId());
			user.setUserTypeId(companyUserType.getUserTypeId());
			User newUser = userRepo.save(user);
			if(userDto.getCompanyBranchId() != null) {
				CompanyUserBranch  companyUserBranch = null;
				List<CompanyUserBranch>  companyUserBranchList = null;
				for(Integer companyBranchId : userDto.getCompanyBranchId()) {
					CompanyBranch companyBranchData = companyBranchRepo.findByCompanyBranchId(companyBranchId);
					companyUserBranchList = companyUserBranchRepo.findByBranch(companyBranchData.getBranch());
					if(companyUserBranch == null) {
					 companyUserBranch = new CompanyUserBranch();
					} else {
						companyUserBranch = companyUserBranchList.get(0);
					}
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
			response.setData(user.getUserId().toString());
			response.setMessage(MotorAuthorityConstants.COMPANY_USER_EDITED);
		}

		response.setStatus(200);
		return response;
	}

	public Response<String> userRemoval(Integer userId, Integer loginId) {
		Response<String> response = new Response<>();
		User user = userRepo.findByUserId(userId);
		if (ObjectUtils.isEmpty(user))
			throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_USER_NOT_FOUND);

		user.setStatus("I");
		user.setUpdatedBy(loginId);
		user.setUpdatedDate(LocalDateTime.now());
		userRepo.save(user);
		response.setData(user.getUserId().toString());
		response.setMessage(MotorAuthorityConstants.COMPANY_USER_REMOVE);
		response.setStatus(200);
		return response;
		
	}
	
	public Response<String> userUnlock(Integer userId, Integer loginId) {
		Response<String> response = new Response<>();
		User user = userRepo.findByUserId(userId);
		if (ObjectUtils.isEmpty(user))
			throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_USER_NOT_FOUND);

		user.setStatus("A");
		user.setUpdatedBy(loginId);
		user.setUpdatedDate(LocalDateTime.now());
		userRepo.save(user);
		response.setData(user.getUserId().toString());
		response.setMessage(MotorAuthorityConstants.COMPANY_USER_UNLOCK);
		response.setStatus(200);
		return response;

	}

	 @SuppressWarnings("resource")
	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = RuntimeException.class)
	public Response<String> importCompanyusers(MultipartFile files, Integer userId) throws IOException {
//		InsuranceCompany companyData =  companyRepo.findByCompanyId(companyId);
//		if(companyData == null)
//			throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_NOT_FOUND);	
		
		Response<String> response = new Response<String>();
		Map<Integer,String> headerOrderMap = new HashMap<>();
		   XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
		   
	        XSSFSheet worksheet = workbook.getSheetAt(0);
	        XSSFRow headerrow = worksheet.getRow(0);
	        headerrow.getPhysicalNumberOfCells();
	        for(int cell = 0; cell< headerrow.getPhysicalNumberOfCells(); cell++) {
	        	headerOrderMap.put(cell+1, headerrow.getCell(cell).getStringCellValue());
	        }
	       
	        if( sheetHeaderValidation(headerOrderMap)) {
	        	User user  = null;
	        	Map<String, Integer> userNameMap = new HashMap<>();
	        	Set<String> companyNameCheck = new HashSet<String>();
	        	if (worksheet.getPhysicalNumberOfRows() > 1) {
	        		for (int row = 1; row < worksheet.getPhysicalNumberOfRows(); row++) {
			    		   XSSFRow rowData = worksheet.getRow(row);
			    		   if (null == rowData.getCell(4)) {

			    			   response.setStatus(400);
			    			   response.setMessage(headerrow.getCell(4).getStringCellValue() + " is empty");
			    			   return response;
			    		   } else {

			    			   if(userNameMap.containsKey(rowData.getCell(4).getStringCellValue())) {
			    				   response.setStatus(400);
			    				   response.setMessage("Your Sheet have Duplicate UserName Entry : "+rowData.getCell(4).getStringCellValue()+" Row At : "+row);
			    				   return response;
			    			   } else {
			    				   userNameMap.put(rowData.getCell(4).getStringCellValue(), row);
			    			   }
			    		   }
			    		   if (null == rowData.getCell(7)) {

			    			   response.setStatus(400);
			    			   response.setMessage(headerrow.getCell(7).getStringCellValue() + " is empty");
			    			   return response;
			    		   } else {
			    			   companyNameCheck.add(rowData.getCell(7).getStringCellValue());
			    		   }
		        	}
		        	String userNamemessage = userNameValidation(userNameMap);
		        	if(!"success".equalsIgnoreCase(userNamemessage)) {
		        		response.setStatus(400);
						response.setMessage(userNamemessage);
						 return response;
		        	}
		        	String companyNameMessage = companyNameValidation(companyNameCheck);
		        	if(!"success".equalsIgnoreCase(companyNameMessage)) {
		        		response.setStatus(400);
						response.setMessage(companyNameMessage);
						 return response;
		        	}
		        try {

	        		String role;
	        		RolesMaster rolesMaster = null;
	        		List<User> userList = new ArrayList<>();	
		        	for (int row = 1; row < worksheet.getPhysicalNumberOfRows(); row++) {
			    		   XSSFRow rowData = worksheet.getRow(row);
			    		   user = new User();
			    		   rolesMaster = null;
			    		   user.setFirstName(rowData.getCell(0).getStringCellValue());
			    		   if (rowData.getCell(1) != null) {
			    			   user.setMiddleName(rowData.getCell(1).getStringCellValue());
			    		   }
			    		   user.setLastName(rowData.getCell(2).getStringCellValue());
			    		   user.setMobileNumber(rowData.getCell(3).getRawValue());
			    		   user.setUserName(rowData.getCell(4).getStringCellValue());
			    		   if (rowData.getCell(5) == null || rowData.getCell(5).getStringCellValue().isEmpty()) {
			    			   response.setStatus(500);
			    			   response.setMessage("Email is empty.");
			    			   return response;
			    		   }
			    		   user.setEmail(rowData.getCell(5).getStringCellValue());
			    		   user.setDesingnation(rowData.getCell(6).getStringCellValue());
			    		   InsuranceCompany companyData =  companyRepo.findByCompanyName(rowData.getCell(7).getStringCellValue());
			    		   user.setInsureCompany(companyData.getCompanyName());
			    		   CompanyUserType  companyUserType =companyUserTypeRepo.findByUserType(rowData.getCell(8).getStringCellValue());
			    		   if (companyUserType != null) {
			    			   user.setUserTypeId(companyUserType.getUserTypeId());
			    		   }  else {
			    			   response.setStatus(500);
			    			   response.setMessage("Invalid User Type.");
			    			   return response;
			    		   }
			    		   if (!StringUtils.isEmpty(rowData.getCell(9))) {
			    			   role = rowData.getCell(9).getStringCellValue();
							   rolesMaster = rolesMasterRepo.findByRoleId(role);
			    		   }
			    		   if (rolesMaster != null) {
			    			   user.setRoleId(rolesMaster.getMasterId());
			    		   } else {
			    			   response.setStatus(500);
			    			   response.setMessage("Invalid Role.");
			    			   return response;
			    		   }
			    		   DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
			   			   user.setPassword(DesPasswordEncoder.encryptPassword(generationUtils.generateRandomPassword()));
			   			   user.setStatus("A");
						   user.setCompanyId(companyData.getCompanyId());
						   user.setUserAccount("CU");
						   user.setCreadtedDate(LocalDateTime.now());
						   user.setCreadtedBy(userId);
						   user.setMailStatus("N");
						   user.setIsPasswordUpdated("N");
						   userList.add(user);
		        	}
		        	userRepo.saveAll(userList);
		        	
		        	response.setStatus(200);
					response.setMessage(MotorAuthorityConstants.NEW_COMPANY_USER_IMPORTED);
		        	}catch(RuntimeException e) {
		        		response.setStatus(400);
						response.setMessage("Your sheet have invalid data.");
						return response;
		        	}
	        	} else {
	        		response.setStatus(400);
					response.setMessage("There are no company users to add from the sheet...");
					return response;

	        	}
	        	
	        }else {
	        	response.setStatus(500);
				response.setMessage("Your Sheet have Invalid Headers....");
	        }
	        
		return response;
	}
	

		private String companyNameValidation(Set<String> companyNameCheck) {
			Set<String> companyNames = companyRepo.getAllCompanyNames();
			String msg = "success";
			for(String company : companyNameCheck) {
				if(!companyNames.contains(company.trim())) {
					msg = "Your Sheet have Invalid Company Name : "+company;
					return msg;
				}
			}
		return msg;
	}

		private String userNameValidation(Map<String, Integer> userNameMap) {
			List<String> userNames = userRepo.getAllUserNames();
			String msg = "success";
			if(userNames != null) {
				for(String username : userNames) {
					if(userNameMap.containsKey(username)) {
						msg	= "Your Sheet have Already Registered UserName  : "+username+" Row At : "+userNameMap.get(username);
					return msg;
					}
				
			}

				}
			return msg;
		}


	
	private boolean sheetHeaderValidation(Map<Integer, String> headerOrderMap) {
		// TODO Auto-generated method stub
		
		if(headerOrderMap.size()>10){
			 return false;
	       }
		
		else if(!headerOrderMap.get(1).equals("First Name")){
			 return false;
	       }
		 else if(!headerOrderMap.get(2).equals("Middle Name")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(3).equals("Last Name")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(4).equals("Mobile Number")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(5).equals("User Name")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(6).equals("Email")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(7).equals("Designation")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(8).equals("Insure Company")){
	    	   return false;

	       }else if(!headerOrderMap.get(9).equals("User Type")){
	    	   return false;

	       }else if(!headerOrderMap.get(10).equals("Role Id")){
	    	   return false;

	       }else {
	    	   return true;
	       }
		 
		
	}

	public Response<AllAuthorityUsersDto> getAuthorityUsers(Integer pageSize, Integer currentPage, String dir, String prop, String value) {
		Response<AllAuthorityUsersDto> response = new Response<>();
		AllAuthorityUsersDto allUsersDTO = new AllAuthorityUsersDto();
		String sortingStr = "";
		if(!StringUtils.isEmpty(value)) {
			sortingStr = "AND user.userName like '%"+value+"%' ";
		}
		if (!StringUtils.isEmpty(prop)) {
			sortingStr += "order by ";
			if ("userName".equalsIgnoreCase(prop))
				sortingStr += "user.userName ";			
			sortingStr += dir;
		}
		String query = "Select user from User user where user.userAccount =\'AU\' " + sortingStr;
		List<User> users = entityManager
				.createQuery(query, User.class)
				.setFirstResult(currentPage * pageSize)
				.setMaxResults(pageSize).getResultList();
		
		
		List<UsersAuthorityDto> listDto = new ArrayList<UsersAuthorityDto>();

		for (User user : users) {

			UsersAuthorityDto userDto = new UsersAuthorityDto();
			userDto.setUserId(user.getUserId());
			userDto.setUserName(user.getUserName());
			if ("A".equals(user.getStatus())) {
				userDto.setStatus("Active");
			} else if ("AL".equals(user.getStatus())) {
				userDto.setStatus("Locked");
			} else if ("I".equals(user.getStatus())) {
				userDto.setStatus("Removed");
			}

			listDto.add(userDto);
		}

		allUsersDTO.setAuthorityusers(listDto);

		allUsersDTO.setTotalCount(entityManager
				.createQuery(query, User.class)
				.getResultList().size());
		response.setData(allUsersDTO);
		response.setStatus(200);

		return response;

	}

	@SuppressWarnings("resource")
	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = RuntimeException.class)
	public Response<String> importAuthorityUsers(MultipartFile files, Integer userId) throws IOException {
		// TODO Auto-generated method stub
		Response<String> response = new Response<String>();
		Map<Integer,String> headerOrderMap = new HashMap<>();
		   XSSFWorkbook workbook = new XSSFWorkbook(files.getInputStream());
		   
	        XSSFSheet worksheet = workbook.getSheetAt(0);
	        XSSFRow headerrow = worksheet.getRow(0);
	        headerrow.getPhysicalNumberOfCells();
	        for(int cell = 0; cell< headerrow.getPhysicalNumberOfCells(); cell++) {
	        	headerOrderMap.put(cell+1, headerrow.getCell(cell).getStringCellValue());
	        }
	       
	        if(AuthoritySheetHeaderValidation(headerOrderMap)) {
	        	User user  = null;
	        	Map<String, Integer> userNameMap = new HashMap<>();
	        	if (worksheet.getPhysicalNumberOfRows() > 1) {

	        		for (int row = 1; row < worksheet.getPhysicalNumberOfRows(); row++) {
			    		   XSSFRow rowData = worksheet.getRow(row);
			    		   if (null == rowData.getCell(4)) {

			    			   response.setStatus(500);
			    			   response.setMessage(headerrow.getCell(4).getStringCellValue() + " is empty");
			    			   return response;
			    		   } else {

			    			   if(userNameMap.containsKey(rowData.getCell(4).getStringCellValue())) {
			    				   response.setStatus(500);
			    				   response.setMessage("Your Sheet have Duplicate UserName Entry : "+rowData.getCell(4).getStringCellValue()+" Row At : "+row);
			    				   return response;
			    			   } else {
			    				   userNameMap.put(rowData.getCell(4).getStringCellValue(), row);
			    			   }
			    		   }
		        	}
		        	String userNamemessage = userNameValidation(userNameMap);
		        	if(!"success".equalsIgnoreCase(userNamemessage)) {
		        		response.setStatus(500);
		        		response.setMessage(userNamemessage);
		        		return response;
		        	}
		        try {
		        		String role;
		        		RolesMaster rolesMaster = null;
		        		List<User> userList = new ArrayList<>();
		        	for (int row = 1; row < worksheet.getPhysicalNumberOfRows(); row++) {
			    		   XSSFRow rowData = worksheet.getRow(row);
			    		   user = new User();
			    		   rolesMaster = null;
			    		   user.setFirstName(rowData.getCell(0).getStringCellValue());
			    		   if (rowData.getCell(1) != null) {
			    			   user.setMiddleName(rowData.getCell(1).getStringCellValue());
			    		   }
			    		   user.setLastName(rowData.getCell(2).getStringCellValue());
			    		   user.setMobileNumber(rowData.getCell(3).getRawValue());
			    		   user.setUserName(rowData.getCell(4).getStringCellValue());
			    		   if (rowData.getCell(5) == null || rowData.getCell(5).getStringCellValue().isEmpty()) {
			    			   response.setStatus(500);
			    			   response.setMessage("Email is empty.");
			    			   return response;
			    		   }
			    		   user.setEmail(rowData.getCell(5).getStringCellValue());
			    		   user.setDesingnation(rowData.getCell(6).getStringCellValue());
			    		   if (!StringUtils.isEmpty(rowData.getCell(7))) {
			    			   role = rowData.getCell(7).getStringCellValue();
							   rolesMaster = rolesMasterRepo.findByRoleId(role);
			    		   }
			    		   if (rolesMaster != null) {
			    			   user.setRoleId(rolesMaster.getMasterId());
			    		   } else {
			    			   response.setStatus(500);
			    			   response.setMessage("Invalid Role.");
			    			   return response;
			    		   }
			    		   DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
			   			   user.setPassword(DesPasswordEncoder.encryptPassword(generationUtils.generateRandomPassword()));
			   			   user.setStatus("A");
						   user.setUserAccount("AU");
						   user.setCreadtedDate(LocalDateTime.now());
						   user.setCreadtedBy(userId);
						   user.setMailStatus("N");
						   user.setIsPasswordUpdated("N");
						   userList.add(user);
						  
		        	}
		        	userRepo.saveAll(userList);
		        	response.setStatus(200);
					response.setMessage(MotorAuthorityConstants.NEW_AUTHORITY_USER_IMPORT);
		        	}catch(RuntimeException e) {
		        		response.setStatus(400);
						response.setMessage("Your sheet have invalid data.");
						return response;
		        	}
	        	} else {
	        		response.setStatus(500);
					response.setMessage("There are no authority users to add from the sheet...");
					return response;
	        	}
	        }else {
	        	response.setStatus(500);
				response.setMessage("Your Sheet have Invalid Headers....");
	        }
	        
		return response;
	}
	
	private boolean AuthoritySheetHeaderValidation(Map<Integer, String> headerOrderMap) {
		
		if(headerOrderMap.size()>8){
			 return false;
	       }
		
		else if(!headerOrderMap.get(1).equals("First Name")){
			 return false;
	       }
		 else if(!headerOrderMap.get(2).equals("Middle Name")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(3).equals("Last Name")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(4).equals("Mobile Number")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(5).equals("User Name")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(6).equals("Email")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(7).equals("Designation")){
	    	   return false;
	       }
		 else if(!headerOrderMap.get(8).equals("Role Id")){
	    	   return false;
	       }else {
	    	   return true;
	       }
		 
	}
	
	public Response<AllUsersDTO> getAllTrafficAuthorityUsers(int pageSize, int currentPage, String dir, String prop, String value) {

		Response<AllUsersDTO> response = new Response<>();
		AllUsersDTO allUsersDTO = new AllUsersDTO();
		String sortingStr = "";
		if(!StringUtils.isEmpty(value)) {
			sortingStr = "AND user.userName like '%"+value+"%' ";
		}
		if (!StringUtils.isEmpty(prop)) {
			sortingStr += "order by ";
			if ("userName".equalsIgnoreCase(prop))
				sortingStr += "user.userName ";			
			sortingStr += dir;
		}
		String query = "Select user from User user where user.userAccount =\'TU\' " + sortingStr;
		List<User> userList = entityManager
				.createQuery(query, User.class)
				.setFirstResult(currentPage * pageSize)
				.setMaxResults(pageSize).getResultList();
		
		List<UsersDto> listDto = new ArrayList<UsersDto>();

		for (User user : userList) {

			UsersDto userDto = new UsersDto();
			userDto.setUserId(user.getUserId());
			userDto.setUserName(user.getUserName());
			userDto.setCompanyId(user.getCompanyId());
			InsuranceCompany company = companyRepo.findByCompanyId(user.getCompanyId());

			if (!ObjectUtils.isEmpty(company)) {
				userDto.setCompanyName(company.getCompanyName());
			}
			
			if ("A".equals(user.getStatus())) {
				userDto.setStatus("Active");
			} else if ("AL".equals(user.getStatus())) {
				userDto.setStatus("Locked");
			} else if ("I".equals(user.getStatus())) {
				userDto.setStatus("Removed");
			}

			listDto.add(userDto);
		}

		allUsersDTO.setUsersDto(listDto);

		allUsersDTO.setTotalCount(entityManager
				.createQuery(query, User.class)
				.getResultList().size());
		response.setData(allUsersDTO);
		response.setStatus(200);

		return response;
	}
	
	public Response<String> editOrAddTrafficAuthorityUser(CompanyUserDto userDto) {

		Response<String> response = new Response<>();

		if (StringUtils.isEmpty(userDto.getUserId())) {
			
			User userData = userRepo.findByUserName(userDto.getUserName());
			if (userData != null) {

				response.setMessage(MotorAuthorityConstants.USERNAME_UNIQUE);
				response.setStatus(200);
				return response;
			}

			User user = modelMapper.map(userDto, User.class);
			user.setStatus("A");
			user.setCompanyId(userDto.getCompanyId());
			user.setUserAccount("TU");
			user.setCreadtedDate(LocalDateTime.now());
			user.setCreadtedBy(userDto.getCreatedBy());
			user.setDesingnation(userDto.getDesingnation());
			user.setIsPasswordUpdated("Y");
			DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
			user.setPassword(DesPasswordEncoder.encryptPassword(userDto.getPassword()));
			if (!StringUtils.isEmpty(userDto.getSignature())) {
				user.setSignature(Base64.getDecoder().decode(userDto.getSignature()));
			}
			user.setMailStatus("N");
			User newUser = userRepo.save(user);
			response.setData(newUser.getUserId().toString());
			response.setMessage(MotorAuthorityConstants.NEW_CTRAFFIC_AUTHORITY_USER_ADDED);
		} else {

			User user = userRepo.findByUserId(userDto.getUserId());

			if (ObjectUtils.isEmpty(user)) {
				throw new BadDataExceptionHandler(MotorAuthorityConstants.TRAFFIC_USER_NOT_FOUND);
			}

			modelMapper.map(userDto, user);
			user.setUpdatedBy(userDto.getUserId());
			user.setUpdatedDate(LocalDateTime.now());
			DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
			user.setPassword(DesPasswordEncoder.encryptPassword(userDto.getPassword()));
			if (!StringUtils.isEmpty(userDto.getSignature())) {
				user.setSignature(Base64.getDecoder().decode(userDto.getSignature()));
			}
			userRepo.save(user);
			response.setData(user.getUserId().toString());
			response.setMessage(MotorAuthorityConstants.TRAFFIC_USER_EDITED);
		}

		response.setStatus(200);
		return response;
	}
	
}
