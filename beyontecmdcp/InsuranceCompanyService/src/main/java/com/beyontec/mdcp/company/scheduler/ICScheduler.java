package com.beyontec.mdcp.company.scheduler;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.beyontec.mdcp.company.model.CertificateAllocation;
import com.beyontec.mdcp.company.model.CertificateDetails;
import com.beyontec.mdcp.company.model.CertificateSerialNum;
import com.beyontec.mdcp.company.model.User;
import com.beyontec.mdcp.company.repo.CertificateAllocationRepo;
import com.beyontec.mdcp.company.repo.CertificateRepo;
import com.beyontec.mdcp.company.repo.CertificateSerialRepo;
import com.beyontec.mdcp.company.repo.UserRepo;
import com.beyontec.mdcp.company.service.DocumentService;
import com.beyontec.mdcp.company.util.InsuranceCompanyConstants;

@Component
public class ICScheduler {
	
	@Autowired
	private CertificateAllocationRepo certificateAllocationRepo;
	

	@Autowired
	private CertificateSerialRepo certificateSerialRepo;

	@Autowired
	private CertificateRepo certificateRepo;

    @Autowired
	private UserRepo userRepo;
    
    @Autowired
	private DocumentService documentService;
    
	@Scheduled(fixedRate  = 1000)
	private void certificateNumberGenerator() {
		
		List<CertificateAllocation> certificateAllocationList = certificateAllocationRepo.findByCertificateNumStatus(1);
		System.out.println("ICScheduler Runing>>>>>>>>>>>>>>>>>");
		if(certificateAllocationList != null && certificateAllocationList.size() > 0) {
			for(CertificateAllocation certificateAllocation : certificateAllocationList) {
				
				addNewCertificate(certificateAllocation);
				certificateAllocation.setCertificateNumStatus(0);
				certificateAllocationRepo.save(certificateAllocation);
				
			}
		}
		
	}
	
	private void addNewCertificate(CertificateAllocation certificateAllocation) {		
		
		  for(int i=0; i<certificateAllocation.getAllocatedCertificates(); i++) {
			  Long  serialData = certificateSerialRepo.getCountCertSerialOrder();
			   CertificateSerialNum certificateSerialNum = new CertificateSerialNum();
			   if(serialData == null ) {
				   certificateSerialNum.setSerialNum("C-"+1);
				   certificateSerialNum.setCompany(certificateAllocation.getCompany());
				   certificateSerialNum.setSerialNumOrder((long) 1);
				   certificateSerialNum.setAllocatedDate(LocalDate.now());
				   certificateSerialNum.setIssuedStatus("N");
					 certificateSerialRepo.save(certificateSerialNum);
			   }else {
				   serialData = serialData+1;
			 // String serialSeq = String.format("%07d", orderSeq);
			  certificateSerialNum.setCompany(certificateAllocation.getCompany());
			  certificateSerialNum.setSerialNum("C-"+serialData);
			  certificateSerialNum.setSerialNumOrder(serialData);
			  certificateSerialNum.setIssuedStatus("N");
			  certificateSerialNum.setAllocatedDate(LocalDate.now());
			  certificateSerialRepo.save(certificateSerialNum);
			  
			   }
		  }
		  
		
		}
		
	@Scheduled(fixedRate  = 1000)
	private void certificateUploadEmailSent() {
		List<CertificateDetails> details = certificateRepo.findByMailStatus(InsuranceCompanyConstants.STATUS_N);
		if (details != null && details.size() > 0) {
			Integer userId = details.get(0).getUploadedBy();
			User user = null;
			if (userId != null) {
				user = userRepo.findByUserId(userId);
			}
			for (CertificateDetails certificateDetails : details) {
				if (StringUtils.isNotBlank(certificateDetails.getPrimaryEmail())) {
					try {
						documentService.showCertificateByPDF(certificateDetails.getInsuranceCompany(),
								certificateDetails, certificateDetails.getPrimaryEmail(), user);
						certificateDetails.setMailStatus(InsuranceCompanyConstants.STATUS_Y);
						certificateRepo.save(certificateDetails);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
