package com.beyontec.mdcp.authservice.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.beyontec.mdcp.authservice.dto.ChangePasswordDto;
import com.beyontec.mdcp.authservice.dto.ForgotPasswordDto;
import com.beyontec.mdcp.authservice.dto.Login;
import com.beyontec.mdcp.authservice.dto.LoginDto;
import com.beyontec.mdcp.authservice.dto.LoginInformation;
import com.beyontec.mdcp.authservice.dto.RefTokenResDto;
import com.beyontec.mdcp.authservice.dto.RefreshTokenDto;
import com.beyontec.mdcp.authservice.dto.SendMailDto;
import com.beyontec.mdcp.authservice.dto.UserInformation;
import com.beyontec.mdcp.authservice.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.authservice.model.CertificateDetails;
import com.beyontec.mdcp.authservice.model.CompanyBranch;
import com.beyontec.mdcp.authservice.model.CompanyUserBranch;
import com.beyontec.mdcp.authservice.model.CustomerLoginResponse;
import com.beyontec.mdcp.authservice.model.InsuranceCompany;
import com.beyontec.mdcp.authservice.model.PolicyDetails;
import com.beyontec.mdcp.authservice.model.Roles;
import com.beyontec.mdcp.authservice.model.RolesMaster;
import com.beyontec.mdcp.authservice.model.TokenDetail;
import com.beyontec.mdcp.authservice.model.User;
import com.beyontec.mdcp.authservice.model.UserAudit;
import com.beyontec.mdcp.authservice.repository.CertificateRepo;
import com.beyontec.mdcp.authservice.repository.CompanyBranchRepo;
import com.beyontec.mdcp.authservice.repository.CompanyRepo;
import com.beyontec.mdcp.authservice.repository.CompanyUserBranchRepo;
import com.beyontec.mdcp.authservice.repository.RolesMasterRepo;
import com.beyontec.mdcp.authservice.repository.RolesRepo;
import com.beyontec.mdcp.authservice.repository.TokenRepo;
import com.beyontec.mdcp.authservice.repository.UserRepository;
import com.beyontec.mdcp.authservice.response.Response;
import com.beyontec.mdcp.authservice.util.AuthConstants;
import com.beyontec.mdcp.authservice.util.GenerationUtils;
import com.beyontec.mdcp.authservice.util.HandlebarTemplateLoader;
import com.beyontec.mdcp.authservice.util.Util;
import com.github.jknack.handlebars.Template;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.body.RequestBodyEntity;

@Service
public class LoginService {
	
	
	 @Value("${security.oauth2-server-url}")
	    private String authServer;

	    @Value("${security.jwt.grant-type-password}")
	    private String grantType_Password;
	    
	    @Value("${security.jwt.grant-type-refresh_token}")
	    private String grantType_RefreshToken;

	    @Value("${security.oauth2-server.user-id}")
	    private String client_id;

	    @Value("${security.oauth2-server.password}")
	    private String client_secret;
	    
	    @Value("${security.jwt.access.token.validity}")
		private Integer tokenExpiryTime;

	    
	    private SecretKey secretKey;
		private SecretKey passwordKey;
		private static final String KEYS_PATH = "/keys/";

	    
	    
	    @Autowired
		private UserRepository userRepository;
	    
	    @Autowired
		private TokenRepo tokenRepo;
	    
	    @Autowired
		private UserAuditService userAuditService;
	    
	    @Autowired
		private GenerateToken generateToken;
	    
	    @Autowired
		private TokenValidation tokenValidation;
	    
	    @Autowired
		private HandlebarTemplateLoader handlebarTemplateLoader;
	    
		@Autowired
		private SendMailService sendMailService;
		
		@Autowired
		private GenerationUtils generationUtils;
		
		@Autowired
		private CompanyRepo companyRepo;

		@Autowired
		private CertificateRepo certificateRepo;
		
		@Autowired
		private RolesMasterRepo rolesMasterRepo;
		
		@Autowired
		private RolesRepo rolesRepo;
		
		@Autowired
		private CompanyUserBranchRepo companyUserBranchRepo;
		
		@Autowired
		private UserRepository userRepo;
	
	public ResponseEntity loginWithOauthToken(Login login) {
		// TODO Auto-generated method stub
		
		login.setPassword("pass#1word"); 
    	final CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(authServer);
        UsernamePasswordCredentials creds
                = new UsernamePasswordCredentials(client_id, client_secret);
        Header header = null;
        try {
        	header = new BasicScheme().authenticate(creds, httpPost, null);
            httpPost.addHeader(header);
            UserDetails userDetails;
           
        } catch (AuthenticationException ae) {

           // throw new CustomCRAException(ae, ErrorCode.OAUTH_SERVER_ERROR);
        }
        
        String responseBody = null;
        int responseCode = -1;

        try {
        	
        	 RequestBodyEntity request = Unirest.post(authServer)
        			 .basicAuth("BEY_OAuth2_Client", "Test@123")
        			 .header("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
        			 .header(header.getName(), header.getValue())
     			 	.body("------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"grant_type\"\r\n\r\n"+ grantType_Password + "\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"username\"\r\n\r\n" + login.getUsername()+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"password\"\r\n\r\n" +login.getPassword() +"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
        	 HttpResponse<String> response = request.asString();
             responseBody = response.getBody();
             responseCode = response.getStatus();
             client.close();
            
        } catch (Exception e) {
           // throw new CustomCRAException(e, ErrorCode.OAUTH_SERVER_ERROR);
        } 
        if (HttpStatus.valueOf(responseCode) == HttpStatus.BAD_REQUEST) {
         /* int invalidLoginAttemptCount = userService.udpateInvalidLoginAttempt(userId);
            AuthenticationResponse responseObj = UtilService.toObject(responseBody, AuthenticationResponse.class);
            responseObj.invalidLoginAttemptCount = invalidLoginAttemptCount;
            responseBody = UtilService.toJson(responseObj); */
        } else {
            //userService.resetInvalidLoginCountAttempt(userId);
        }
        return new ResponseEntity(responseBody, HttpStatus.valueOf(responseCode));
    	
	}






	public Response<UserInformation> login(LoginDto login) {
		Response<UserInformation> response = new Response<>();
		User userDetail = userRepository.getUserByUserName(login.getUserName());
		if (userDetail == null || userDetail.getCompanyId() != null || !"AU".equals(userDetail.getUserAccount()) ) {
			throw new BadDataExceptionHandler(AuthConstants.USER_NOT_AVAIL);
		} else {
			if("I".equals(userDetail.getStatus())) {
				 response.setMessage(AuthConstants.USER_ACCOUNT_INACTIVE);
				 response.setStatus(200);
				 return response;
			} else if ("AL".equals(userDetail.getStatus())) {
				response.setMessage(AuthConstants.USER_BLOCKED);
				response.setStatus(400);
				return response;
			}

			validateLogin(login, response, userDetail);
		}
		return response;
	}
	
	private void validateLogin(LoginDto login, Response<UserInformation> response, User userDetail) {
		// if(!"D".equals(userDetail.getForcePwdChange())) {
		List<TokenDetail> userTokenDetails = tokenRepo.getUserTokenDetails(userDetail.getUserId());
		/*
		 * if (userTokenDetails != null && !userTokenDetails.isEmpty() &&
		 * LocalDateTime.now().isBefore(userTokenDetails.get(0).getTokenExpiryTime()))
		 * throw new BadDataExceptionHandler(AuthConstants.USER_ALREADY_LOGGED_IN);
		 */
		checkPasswordEquals(login, response, userDetail);

	}
	
	private void checkPasswordEquals(LoginDto login, Response<UserInformation> response, User userDetail) {
		String decryptedPassword = decryptPassword(userDetail.getPassword());
		if (login.getPassword().equals(decryptedPassword)) {
			userAuditService.deleteUserAudit(userDetail.getUserId()); // Check ID MApping.........
			setLoginSuccess(response, userDetail, login.getIsRememberMe(), login);
		} else {
			logUserDetails(userDetail);
			int invalidLoginAttemptCount = userAuditService.getUserAuditCount(userDetail.getUserId());
			if (invalidLoginAttemptCount >= AuthConstants.MAX_INVALID_LOGIN_ATTEMPT_COUNT) {
				try {
					userRepository.blockUser(userDetail.getUserName());
					throw new BadDataExceptionHandler(AuthConstants.USER_BLOCKED);
				} catch (Exception e) {
					throw e;
				}
			}
			int attemptLeft = AuthConstants.MAX_INVALID_LOGIN_ATTEMPT_COUNT - invalidLoginAttemptCount;
			if (attemptLeft > 0) {
				throw new BadDataExceptionHandler(AuthConstants.PASSWORDS_DONT_MATCH_ERROR + attemptLeft);
			} else {
				throw new BadDataExceptionHandler(AuthConstants.PASSWORD_ATTEMPTS_EXCEEDED);
			}
		}
	}
	
	
	private void logUserDetails(User userDetail) {
		UserAudit userAudit = new UserAudit();
		userAudit.setUserId(userDetail.getUserId());
		userAudit.setCreatedBy(userDetail.getUserName());
		userAudit.setUpdatedBy(userDetail.getUserName());
		userAudit.setUpdatedDate(Util.getCurrentDateTime());
		userAudit.setCreatedDate(Util.getCurrentDateTime());
		userAuditService.saveUserAudit(userAudit);
	}
	
	public void setLoginSuccess(Response<UserInformation> response, User userDetail, Integer isRemeberToken,
			LoginDto login) {
		UserInformation userInfo = new UserInformation();
		if (userDetail.getUserId() != null) {
			//userInfo.setUserType(userDetail.getUserType());
	
			userInfo.setFullName(userDetail.getFirstName()+" "+userDetail.getMiddleName()+" "+userDetail.getLastName());
			userInfo.setFirstName(userDetail.getFirstName());
			userInfo.setLastName(userDetail.getLastName());
			LocalDateTime lastLoggedinTime = userDetail.getLastLoggedIn();
			if (lastLoggedinTime != null) {
				ZonedDateTime withTimeZone = ZonedDateTime.of(lastLoggedinTime, ZoneId.systemDefault());
				userInfo.setLastLoggedIn(withTimeZone
						.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)));
			}
		}
		userInfo.setUserName(userDetail.getUserName());
		userInfo.setIsPasswordUpdated(userDetail.getIsPasswordUpdated());
		userInfo.setEmail(userDetail.getEmail());
		if (!StringUtils.isEmpty(userDetail.getLastLoggedIn())) {
			userInfo.setLoggedfirst(userDetail.getLastLoggedIn().toString());
		}
		userInfo.setUserAccount(userDetail.getUserAccount());
		userInfo.setUserType(userDetail.getUserAccount());
		if("CU".equals(userDetail.getUserAccount())) {
			userInfo.setCompanyId(userDetail.getCompanyId());
			InsuranceCompany company = companyRepo.findByCompanyId(userDetail.getCompanyId());
			if (!ObjectUtils.isEmpty(company) && !ObjectUtils.isEmpty(company.getCompanyLogo())) {
				userInfo.setCompanyLogo("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(company.getCompanyLogo()));
				
			}
			
			CompanyUserBranch companyUserBranch = companyUserBranchRepo.findByUserAndPrimaryBranch(userDetail,"Y");
			if(companyUserBranch != null) {
				userInfo.setBranchId(companyUserBranch.getCompanyBranch().getCompanyBranchId());
			}
			userInfo.setCompanyContactNo(company.getContactNo());
			userInfo.setEmail(company.getMailId());
			userInfo.setCompanyName(company.getCompanyName());
			
		}
		
		RolesMaster rolesMaster = rolesMasterRepo.findByMasterId(userDetail.getRoleId());
		
		if (!StringUtils.isEmpty(rolesMaster)) {

			List<Roles> roles = rolesRepo.findByRolesMaster(rolesMaster);
			List<Integer> accessRights = new ArrayList<>();
			for (Roles role : roles) {

				if (role.isCanAccess() == true) {
					accessRights.add(role.getRolesModule().getModuleId());
				}
			}
			userInfo.setAccessRights(accessRights);
		}
		
		// userInfo.setUserType(userDetail.getUserType());
		userInfo.setUserId(userDetail.getUserId());
		if (!StringUtils.isEmpty(userDetail.getPhoto())) {
			userInfo.setUserPhoto("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(userDetail.getPhoto()));
		}
		addTokenDetails(userInfo, userDetail, isRemeberToken, login);
		// setCreditBalance(userInfo);
		userDetail.setLastLoggedIn(LocalDateTime.now());
		userRepository.save(userDetail);
		response.setData(userInfo);
		response.setMessage(AuthConstants.LOGIN_SUCCESS);
		response.setStatus(HttpStatus.OK.value());
	}
	
	@Transactional
	private void addTokenDetails(UserInformation loginDetails, User userDetail, Integer isRemeberToken,
			LoginDto login) {
		//tokenRepo.deleteUserToken(userDetail.getUserId());
		TokenDetail tokenDetail = new TokenDetail();
		tokenDetail.setClientIp("");
		tokenDetail.setDevicetype("");
		tokenDetail.setUserId(userDetail.getUserId());
		tokenDetail.setTokenExpiryTime(LocalDateTime.now().plusMinutes(tokenExpiryTime));
		tokenDetail.setIsRemember(isRemeberToken);
		tokenDetail.setCreadtedBy("System");
		tokenDetail.setUpdatedBy("System");
		tokenDetail.setUpdatedDate(Util.getCurrentDateTime());
		tokenDetail.setCreadtedDate(Util.getCurrentDateTime());
		generateToken.generateToken(tokenDetail, login);
		tokenRepo.save(tokenDetail);
		loginDetails.setToken(tokenDetail.getToken());
		loginDetails.setRefreshToken(tokenDetail.getRefreshToken());
	}

	
	private String decryptPassword(String password) {
		try {
			byte[] decodedPassword = Base64.getMimeDecoder().decode(password.getBytes());
			Cipher cipher = this.createCipher(Cipher.DECRYPT_MODE);
			byte[] utf16 = cipher.doFinal(decodedPassword);
			String decryptedPassword = new String(utf16, "UTF16");
			return decryptedPassword.split("/")[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	@PostConstruct
	public void init() {
		secretKey = this.getSecretKey("key.out");
		passwordKey = this.getSecretKey("pwkey.out");
	}

	private SecretKey getSecretKey(String fileName) {
		SecretKey key = null;
		try (InputStream in = this.getClass().getResourceAsStream(KEYS_PATH + fileName)) {
			byte[] bytes = IOUtils.toByteArray(in);
			DESedeKeySpec keyspec = new DESedeKeySpec(bytes);
			SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
			key = keyfactory.generateSecret(keyspec);
		} catch (Exception ie) {
			ie.printStackTrace();
		}
		return key;
	}

	private String encryptPassword(String password) {
		try {
			password = password + Base64.getEncoder().encodeToString(passwordKey.getEncoded());
			byte[] encodedPassword = password.getBytes("UTF16");
			Cipher cipher = this.createCipher(Cipher.ENCRYPT_MODE);
			byte[] encryptedPassword = cipher.doFinal(encodedPassword);
			return Base64.getEncoder().encodeToString(encryptedPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Cipher createCipher(int cipherMode) {
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("DESede");
			cipher.init(cipherMode, this.secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipher;
	}



	public Response<UserInformation> signOut(String token) {
		Response<UserInformation> response = new Response<>();
		Integer userId = tokenValidation.validateToken(token);
		tokenRepo.deleteUserToken(userId);
		response.setMessage(AuthConstants.LOG_OUT_SUCCESS);
		response.setStatus(HttpStatus.OK.value());
		return response;
	}


	public Response<String> forgotPassword(ForgotPasswordDto requestDto) {
		Response<String> responseDto = new Response<>();
		String randomPassword = null;
		User userDetail = userRepository.getUserByUserName(requestDto.getUserName());
		if (userDetail == null) {
			throw new BadDataExceptionHandler(AuthConstants.REGISTERED_EMAIL_NOT_AVAIL);
		} else {
			if (!requestDto.getEmail().equals(userDetail.getEmail()))
				throw new BadDataExceptionHandler(AuthConstants.EMAIL_NOT_VALID);

			 randomPassword = generationUtils.generateRandomPassword();
			userDetail.setPassword(encryptPassword(randomPassword));
			userDetail.setIsPasswordUpdated("Y");
			userRepository.save(userDetail);

			Map<String, Object> data = new HashMap<>();
			data.put("firstName", userDetail.getFirstName());
			data.put("userName", userDetail.getUserName());
			data.put("password", randomPassword);
			data.put("isInlineImageVisible", "block");

			String htmlContent = null;
			try {
				Template template = handlebarTemplateLoader.getTemplate("ForgetPassword");
				htmlContent = template.apply(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			SendMailDto mailDto = new SendMailDto();
			mailDto.setSubject("MDCP - Password reset");
			mailDto.setMessage(htmlContent);
			mailDto.setToEmail(Arrays.asList(requestDto.getEmail()));
			mailDto.setDisplayEmailSignature(true);
			try {
				sendMailService.sendEmail(mailDto);
			} catch (Exception e) {

				responseDto.setStatus(HttpStatus.OK.value());
				responseDto.setMessage(AuthConstants.FORGOT_PASS_LINK 
						+ " "+ AuthConstants.MAIL_QUOTA_EXCEED);
				return responseDto;
			
			}

			responseDto.setMessage(AuthConstants.FORGOT_PASS_LINK);
			responseDto.setStatus(HttpStatus.OK.value());

		}

		return responseDto;
	}

	
	






	public Response<List<String>> changePassword(ChangePasswordDto changePassword) {
		Response<List<String>> response = new Response<>();
		User user = userRepository.getUserByUserName(changePassword.getUserName());
		if (user != null) {
			String oldPassword = decryptPassword(user.getPassword());
			if (oldPassword != null && oldPassword.equals(changePassword.getOldPassword())) {
				List<String> msgs = generationUtils.validateAndChangePassword(user, changePassword.getNewPassword());
				if (!msgs.isEmpty()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage(AuthConstants.PASSWORD_INAVLD);
					return response;
				} else {
					response.setStatus(HttpStatus.OK.value());
					response.setMessage(AuthConstants.PASS_CHANGE_SUCCESS);

					String encryptedPassword = encryptPassword(changePassword.getNewPassword());
					changePassword.setNewPassword(encryptedPassword);
					user.setPassword(encryptedPassword);
					
					user.setIsPasswordUpdated("N");
					userRepository.save(user);
				}
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage(AuthConstants.INVALID_OLD_PASSWORD);
			}

		} else {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			response.setMessage(AuthConstants.USER_NOT_AVAIL);
		}
		return response;
	}


	public Response<UserInformation> getGuestUserDetails() {

		Response<UserInformation> response = new Response<>();
		User userDetail = userRepository.getUserByUserName("superuser");
		UserInformation userInfo = new UserInformation();
		if (userDetail == null) {
			throw new BadDataExceptionHandler(AuthConstants.USER_NOT_AVAIL);
		} else {
			setGuestLoginSuccess(response, userDetail, 0);
		}

		return response;
	}

	public void setGuestLoginSuccess(Response<UserInformation> response, User userDetail,
			Integer isRemeberToken) {

		UserInformation userInfo = new UserInformation();
		if (userDetail.getUserId() != null) {

			
			userInfo.setFullName(userDetail.getUserName());
			userInfo.setEmail(userDetail.getEmail());
			LocalDateTime lastLoggedinTime = userDetail.getLastLoggedIn();
			if (lastLoggedinTime != null) {
				ZonedDateTime withTimeZone = ZonedDateTime.of(lastLoggedinTime, ZoneId.systemDefault());
				userInfo.setLastLoggedIn(withTimeZone
						.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)));
			}
		}
		userInfo.setUserName(userDetail.getUserName());
		userInfo.setLoggedfirst(userDetail.getIsPasswordUpdated());
		userInfo.setUserId(userDetail.getUserId());
		addGuestTokenDetails(userInfo, userDetail, isRemeberToken);
		userDetail.setLastLoggedIn(LocalDateTime.now());
		userRepository.save(userDetail);
		response.setData(userInfo);
		response.setMessage(AuthConstants.LOGIN_SUCCESS);
		response.setStatus(HttpStatus.OK.value());
	}

	@Transactional
	private void addGuestTokenDetails(UserInformation loginDetails, User userDetail, Integer isRemeberToken) {

		List<TokenDetail> userTokenDetails = tokenRepo.getUserTokenDetails(userDetail.getUserId());
		TokenDetail tokenDetail = null;
		LoginDto loginDto = new LoginDto();
		if (userTokenDetails == null || userTokenDetails.isEmpty()) {
			tokenDetail = new TokenDetail();
			tokenDetail.setClientIp("");
			tokenDetail.setDevicetype("");
			tokenDetail.setUserId(userDetail.getUserId());
			tokenDetail.setIsRemember(isRemeberToken);
			tokenDetail.setCreadtedBy("System");
			tokenDetail.setUpdatedBy("System");
			tokenDetail.setUpdatedDate(Util.getCurrentDateTime());
			tokenDetail.setCreadtedDate(Util.getCurrentDateTime());
			loginDto.setPassword(userDetail.getUserName());
			loginDto.setPassword(userDetail.getPassword());
			loginDto.setIsRememberMe(isRemeberToken);
			generateToken.generateToken(tokenDetail, loginDto);
		} else {
			tokenDetail = userTokenDetails.get(0);
		}
		tokenDetail.setTokenExpiryTime(LocalDateTime.now().plusYears(2));
		tokenRepo.save(tokenDetail);
		loginDetails.setToken(tokenDetail.getToken());
	}

	public Response<RefTokenResDto> getRefreshToken(RefreshTokenDto refreshTokenDto) {
		Response<RefTokenResDto> response = new Response<RefTokenResDto>();
		RefTokenResDto refTokenResDto = new RefTokenResDto();
		generateToken.generateTokenRefresh(refreshTokenDto, refTokenResDto);
		response.setData(refTokenResDto);
		response.setMessage(AuthConstants.LOGIN_SUCCESS);
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

	public Response<UserInformation> companyLogin(LoginDto login) {
		Response<UserInformation> response = new Response<>();
		User userDetail = userRepository.getUserByUserName(login.getUserName());
		
		if (userDetail != null && userDetail.getCompanyId() != null ) {
			if("I".equals(userDetail.getStatus())) {
				 response.setMessage(AuthConstants.USER_ACCOUNT_INACTIVE);
				 response.setStatus(200);
				 return response;
			} else if ("AL".equals(userDetail.getStatus())) {
				response.setMessage(AuthConstants.USER_BLOCKED);
				response.setStatus(400);
				return response;
			}
			InsuranceCompany company = companyRepo.findByCompanyId(userDetail.getCompanyId());
			 if("A".equals(company.getStatus())) {
			if("CU".equals(userDetail.getUserAccount()) || "GU".equals(userDetail.getUserAccount())) {
			validateLogin(login, response, userDetail);
			}else {
				 response.setMessage(AuthConstants.USER_NOT_AVAIL);
				 response.setStatus(200);
				 return response;
			}
			 }else {
				 response.setMessage("Company Account InActive");
				 response.setStatus(200);
				 return response;
			 }
		} else {
			 response.setMessage(AuthConstants.USER_NOT_AVAIL);
			 response.setStatus(200);
			 return response;
		}
		
		return response;
	}
	
	public Response<UserInformation> trafficAuthorityLogin(LoginDto login) {
		Response<UserInformation> response = new Response<>();
		User userDetail = userRepository.getUserByUserName(login.getUserName());
		if (userDetail == null || !"TU".equals(userDetail.getUserAccount())) {
			throw new BadDataExceptionHandler(AuthConstants.USER_NOT_AVAIL);
		} else if ("AL".equals(userDetail.getStatus())) {
			throw new BadDataExceptionHandler(AuthConstants.USER_BLOCKED);
		} else {
			validateLogin(login, response, userDetail);
		}
		return response;
	}

	public Response<CustomerLoginResponse> customerLogin(LoginDto login) {
		
		Response<CustomerLoginResponse> loginResponse = new Response<>();
		Response<UserInformation> response = new Response<>();
		User userDetail = userRepository.getUserByUserName(login.getUserName());
		if (userDetail == null || !"EU".equals(userDetail.getUserAccount())) {
			throw new BadDataExceptionHandler(AuthConstants.USER_NOT_AVAIL);
		} else if ("AL".equals(userDetail.getStatus())) {
			throw new BadDataExceptionHandler(AuthConstants.USER_BLOCKED);
		} else {
			validateLogin(login, response, userDetail);
		}
		
		String mail = response.getData().getEmail();
		
		List<CertificateDetails> certifictaes = certificateRepo.findByPrimaryEmail(mail);
		List<PolicyDetails> policyDetails = new ArrayList<>();
		for(CertificateDetails certificateDetails : certifictaes) {
			PolicyDetails policyDetail = new PolicyDetails();
			policyDetail.setCertificateNo(certificateDetails.getCertificateSerialNumber());
			policyDetail.setPolicyNo(certificateDetails.getPolicyNumber());
			policyDetail.setRegisterNo(certificateDetails.getRegistartionNumber());
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
			
			policyDetail.setExpiryDate(formatter.format(certificateDetails.getExpiryDate()));
			policyDetails.add(policyDetail);
		}
		
		CustomerLoginResponse data = new CustomerLoginResponse();
		data.setUserInformation(response.getData());
		data.setPolicyDetails(policyDetails);
		loginResponse.setData(data);
		loginResponse.setMessage(AuthConstants.LOGIN_SUCCESS);
		loginResponse.setStatus(200);
		return loginResponse;
	}


	@Transactional
	private void getLoginToken(LoginInformation loginInfo, User userDetail,
			LoginDto login) {
		TokenDetail tokenDetail = new TokenDetail();
		tokenDetail.setClientIp("");
		tokenDetail.setDevicetype("");
		tokenDetail.setUserId(userDetail.getUserId());
		tokenDetail.setTokenExpiryTime(LocalDateTime.now().plusMinutes(tokenExpiryTime));
		tokenDetail.setIsRemember(login.getIsRememberMe());
		tokenDetail.setCreadtedBy("System");
		tokenDetail.setUpdatedBy("System");
		tokenDetail.setUpdatedDate(Util.getCurrentDateTime());
		tokenDetail.setCreadtedDate(Util.getCurrentDateTime());
		generateToken.generateToken(tokenDetail, login);
		loginInfo.setToken(tokenDetail.getToken());
		loginInfo.setRefreshToken(tokenDetail.getRefreshToken());
	}

	public Response<LoginInformation> authServiceLogin(LoginDto login) {
		Response<LoginInformation> response = new Response<>();
		User userDetail = userRepository.getUserByUserName(login.getUserName());

		if (userDetail != null && userDetail.getCompanyId() != null) {
			if ("I".equals(userDetail.getStatus())) {
				response.setMessage(AuthConstants.USER_ACCOUNT_INACTIVE);
				response.setStatus(200);
				return response;
			}
			InsuranceCompany company = companyRepo.findByCompanyId(userDetail.getCompanyId());
			if ("A".equals(company.getStatus())) {
				if ("CU".equals(userDetail.getUserAccount()) || "GU".equals(userDetail.getUserAccount())) {
					String decryptedPassword = decryptPassword(userDetail.getPassword());
					if (login.getPassword().equals(decryptedPassword)) {
						userAuditService.deleteUserAudit(userDetail.getUserId()); // Check ID MApping.........
						LoginInformation loginInfo = new LoginInformation();
						addUserTokenDetails(loginInfo, userDetail, login.getIsRememberMe(), login);
						userDetail.setLastLoggedIn(LocalDateTime.now());
						userRepository.save(userDetail);
						response.setData(loginInfo);
						response.setStatus(HttpStatus.OK.value());
					} else {
						logUserDetails(userDetail);
						int invalidLoginAttemptCount = userAuditService.getUserAuditCount(userDetail.getUserId());
						if (invalidLoginAttemptCount >= AuthConstants.MAX_INVALID_LOGIN_ATTEMPT_COUNT) {
							try {
								userRepository.blockUser(userDetail.getUserName());
							} catch (Exception e) {
								System.out.println(e);
							}
						}
						int attemptLeft = AuthConstants.MAX_INVALID_LOGIN_ATTEMPT_COUNT - invalidLoginAttemptCount;
						if (attemptLeft > 0) {
							throw new BadDataExceptionHandler(AuthConstants.PASSWORDS_DONT_MATCH_ERROR + attemptLeft);
						} else {
							throw new BadDataExceptionHandler(AuthConstants.PASSWORD_ATTEMPTS_EXCEEDED);
						}
					}
				} else {
					response.setMessage(AuthConstants.USER_NOT_AVAIL);
					response.setStatus(200);
					return response;
				}
			} else {
				response.setMessage("Company Account InActive");
				response.setStatus(200);
				return response;
			}
		} else {
			response.setMessage(AuthConstants.USER_NOT_AVAIL);
			response.setStatus(200);
			return response;
		}
		return response;
	}
	

	@Transactional
	private void addUserTokenDetails(LoginInformation loginDetails, User userDetail, Integer isRemeberToken,
			LoginDto login) {
		TokenDetail tokenDetail = new TokenDetail();
		tokenDetail.setClientIp("");
		tokenDetail.setDevicetype("");
		tokenDetail.setUserId(userDetail.getUserId());
		tokenDetail.setTokenExpiryTime(LocalDateTime.now().plusMinutes(tokenExpiryTime));
		tokenDetail.setIsRemember(isRemeberToken);
		tokenDetail.setCreadtedBy("System");
		tokenDetail.setUpdatedBy("System");
		tokenDetail.setUpdatedDate(Util.getCurrentDateTime());
		tokenDetail.setCreadtedDate(Util.getCurrentDateTime());
		generateToken.generateToken(tokenDetail, login);
		tokenRepo.save(tokenDetail);
		loginDetails.setToken(tokenDetail.getToken());
		loginDetails.setRefreshToken(tokenDetail.getRefreshToken());
	}
	
	public Response<UserInformation> authServiceLoginDetails(LoginDto login) {
		
		return companyLogin(login);
	}
	
	public Response<String> userUnlock(Integer userId, Integer loginId) {
		Response<String> response = new Response<>();

		String randomPassword = null;
		User user = userRepo.findByUserId(userId);
		if (ObjectUtils.isEmpty(user)) {
			throw new BadDataExceptionHandler(AuthConstants.USER_NOT_FOUND);
		}

		randomPassword = generationUtils.generateRandomPassword();
		user.setStatus("A");
		user.setUpdatedBy(loginId);
		user.setUpdatedDate(LocalDateTime.now());
		user.setPassword(encryptPassword(randomPassword));
		userRepo.save(user);
		response.setData(user.getUserId().toString());

		Map<String, Object> data = new HashMap<>();
		data.put("firstName", user.getFirstName());
		data.put("userName", user.getUserName());
		data.put("password", randomPassword);
		data.put("isInlineImageVisible", "block");

		String htmlContent = null;
		try {
			Template template = handlebarTemplateLoader.getTemplate("activateUser");
			htmlContent = template.apply(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SendMailDto mailDto = new SendMailDto();
		mailDto.setSubject("Activate User");
		mailDto.setMessage(htmlContent);
		mailDto.setToEmail(Arrays.asList(user.getEmail()));
		mailDto.setDisplayEmailSignature(true);
		try {
			sendMailService.sendEmail(mailDto);
		} catch (Exception e) {

			response.setStatus(HttpStatus.OK.value());
			response.setMessage(AuthConstants.USER_ACTIVATED 
					+ " "+ AuthConstants.MAIL_QUOTA_EXCEED);
			return response;
		
		}

		response.setMessage(AuthConstants.USER_ACTIVATED);
		response.setStatus(200);
		return response;

	}
	

}
