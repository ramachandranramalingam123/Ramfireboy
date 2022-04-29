package com.beyontec.mdcp.authservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.beyontec.mdcp.authservice.model.CertificateDetails;
import com.beyontec.mdcp.authservice.model.SignUp;
import com.beyontec.mdcp.authservice.model.User;
import com.beyontec.mdcp.authservice.repository.CertificateRepo;
import com.beyontec.mdcp.authservice.repository.UserRepository;
import com.beyontec.mdcp.authservice.response.Response;
import com.beyontec.mdcp.authservice.security.password.DesPasswordEncoder;
import com.beyontec.mdcp.authservice.util.AuthConstants;

@Service
public class EndCustomerService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private CertificateRepo certificateRepo;
	
	public Response<String> createCustomer(SignUp signUp) {

		Response<String> response = new Response<>();

		Optional<User> existingUser = userRepo.findByUserName(signUp.getUserId());

		if (!ObjectUtils.isEmpty(existingUser)) {

			response.setMessage(AuthConstants.CUSTOMER_ID_ALREADY_EXIST);
			response.setStatus(400);
			return response;
		}

		List<CertificateDetails> certificateDetails = certificateRepo.findByPrimaryEmail(signUp.getEmailId());

		if (certificateDetails.size() == 0) {
			response.setMessage(AuthConstants.NO_CERTFICATES_AVAILBLE_MAIL);
			response.setStatus(400);
			return response;
		}

		if (!StringUtils.isEmpty(signUp.getPolicyNo())) {

			List<CertificateDetails> certificateDetailsList = certificateRepo.findByPolicyNumber(signUp.getPolicyNo());

			if (certificateDetailsList.size() == 0) {
				response.setMessage(AuthConstants.NO_CERTFICATE_AVAILBLE_POLICYNO);
				response.setStatus(400);
				return response;
			}
		}

		User user = new User();
		user.setUserName(signUp.getUserId());
		user.setStatus("A");
		user.setUserAccount("EU");
		user.setCreadtedDate(LocalDateTime.now());
		// user.setIsPasswordUpdated("Y");
		DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
		user.setPassword(DesPasswordEncoder.encryptPassword(signUp.getPassword()));
		user.setEmail(signUp.getEmailId());

		User newUser = userRepo.save(user);

		response.setData(newUser.getUserId().toString());
		response.setMessage(AuthConstants.CUSTOMER_CREATED_SUCCESSFULLY);
		response.setStatus(200);
		return response;
	}
}
