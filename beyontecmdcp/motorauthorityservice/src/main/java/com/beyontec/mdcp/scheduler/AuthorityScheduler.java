package com.beyontec.mdcp.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.beyontec.mdcp.dto.SendMailDto;
import com.beyontec.mdcp.model.CertificateAllocation;
import com.beyontec.mdcp.model.InsuranceCompany;
import com.beyontec.mdcp.model.User;
import com.beyontec.mdcp.repo.CertificateAllocationRepo;
import com.beyontec.mdcp.repo.CompanyRepo;
import com.beyontec.mdcp.repo.UserRepo;
import com.beyontec.mdcp.service.SendMailService;
import com.beyontec.mdcp.util.DesPasswordEncoder;
import com.beyontec.mdcp.util.HandlebarTemplateLoader;
import com.github.jknack.handlebars.Template;



@Component
public class AuthorityScheduler {
	
	@Autowired
	private CompanyRepo companyRepo;
	
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private CertificateAllocationRepo certificateAllocationRepo;
	
	@Autowired
	private HandlebarTemplateLoader handlebarTemplateLoader;
	
    
	@Autowired
	private SendMailService sendMailService;
	
	@Scheduled(fixedRate  = 1000)
	public void autoCertificateAllocationByAuthority() {
		System.out.println("Scheduler Running................");
		List<CertificateAllocation> certificateAllocationList = certificateAllocationRepo.findByShowAuthorityNonifyAndRequestedDateNotNull(1);
		if(certificateAllocationList != null ) {
			
			for(CertificateAllocation certificateAllocation : certificateAllocationList) {
				LocalDateTime tempDateTime = LocalDateTime.from( certificateAllocation.getRequestedDate() );
				long days = tempDateTime.until( LocalDateTime.now(), ChronoUnit.DAYS );
				InsuranceCompany companyData = companyRepo.findByCompanyId(certificateAllocation.getCompany().getCompanyId());
			
				if(companyData.getAutoApprovalTime() <= days && companyData.getAutoApprovalLimit()>=certificateAllocation.getRequestedCertificates()) {
					
					certificateAllocation.setAllocatedCertificates(certificateAllocation.getRequestedCertificates());
					certificateAllocation.setAllocatedBy(1);
					certificateAllocation.setAllocatedDate(LocalDateTime.now());
					certificateAllocation.setShowAuthorityNonify(0);
					certificateAllocation.setShowCompanyNonify(1);
					certificateAllocationRepo.save(certificateAllocation);
					
					
				}
			}
			
			
		}
	}
	
	@Scheduled(fixedRate  = 1000)
	public void companyUserEmail() {
		List<User> users = userRepo.findByMailStatus("N");
		if(users != null &&  users.size() > 0) {
		for(User userDetail : users) {
		Map<String, Object> data = new HashMap<>();
		data.put("firstName", userDetail.getFirstName());
		data.put("userName", userDetail.getUserName());
		 DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
		data.put("password", DesPasswordEncoder.decryptPassword(userDetail.getPassword()));
		data.put("isInlineImageVisible", "block");

		String htmlContent = null;
		try {
			Template template = handlebarTemplateLoader.getTemplate("UsersEmail");
			htmlContent = template.apply(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SendMailDto mailDto = new SendMailDto();
		mailDto.setSubject("MDCP - User creation");
		mailDto.setMessage(htmlContent);
		mailDto.setToEmail(Arrays.asList(userDetail.getEmail()));
		mailDto.setDisplayEmailSignature(true);
		userDetail.setMailStatus("Y");
		userRepo.save(userDetail);
		try {
			sendMailService.sendEmail(mailDto);
		} catch (Exception e) {
			// TODO handle Exception
		}
		}
	}
	}

}
