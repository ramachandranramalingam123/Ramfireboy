package com.beyontec.mdcp.company.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.beyontec.mdcp.company.dto.CertificateCompanyNotifyDto;
import com.beyontec.mdcp.company.dto.CompanyAddDto;
import com.beyontec.mdcp.company.dto.CompanyBranchAddDto;
import com.beyontec.mdcp.company.dto.CompanyUserTypesDto;
import com.beyontec.mdcp.company.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.company.model.BranchMaster;
import com.beyontec.mdcp.company.model.CertificateAllocation;
import com.beyontec.mdcp.company.model.CertificateSerialNum;
import com.beyontec.mdcp.company.model.CompanyBranch;
import com.beyontec.mdcp.company.model.CompanyUserBranch;
import com.beyontec.mdcp.company.model.CompanyUserType;
import com.beyontec.mdcp.company.model.InsuranceCompany;
import com.beyontec.mdcp.company.model.User;
import com.beyontec.mdcp.company.repo.BranchMasterRepo;
import com.beyontec.mdcp.company.repo.CertificateAllocationRepo;
import com.beyontec.mdcp.company.repo.CertificateSerialRepo;
import com.beyontec.mdcp.company.repo.CompanyBranchRepo;
import com.beyontec.mdcp.company.repo.CompanyRepo;
import com.beyontec.mdcp.company.repo.CompanyUserBranchRepo;
import com.beyontec.mdcp.company.repo.CompanyUserTypeRepo;
import com.beyontec.mdcp.company.repo.UserRepo;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.util.InsuranceCompanyConstants;







@Service
public class CompanyService {

	

	
	@Autowired
	private CompanyUserTypeRepo companyUserTypeRepo;
	
	@Autowired
	private CompanyRepo companyRepo;



	@Autowired
	private CertificateSerialRepo certificateSerialRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private CertificateAllocationRepo certificateAllocationRepo;
	
	@Autowired
	private BranchMasterRepo branchMasterRepo;
	
	@Autowired
	private CompanyBranchRepo companyBranchRepo;
	
	@Autowired
	private CompanyUserBranchRepo companyUserBranchRepo;
	


	public Response<List<CertificateCompanyNotifyDto>> companyNotiifyCertificates(Integer userId, Integer companyId) {
		Response<List<CertificateCompanyNotifyDto>> response = new Response<List<CertificateCompanyNotifyDto>>();
		List<CertificateCompanyNotifyDto> certificateCompanyNotifyDtoList = new ArrayList<>();
		CertificateCompanyNotifyDto certificateCompanyNotifyDto = null;
		InsuranceCompany companyData = companyRepo.findByCompanyId(companyId);
		List<CertificateAllocation> certificateAllocationList = certificateAllocationRepo.findByShowCompanyNonifyAndCompany(1,companyData);
		if(certificateAllocationList != null &&  0 < certificateAllocationList.size()) {
			
			
			for(int i=0; i<certificateAllocationList.size(); i++) {
				certificateCompanyNotifyDto = new CertificateCompanyNotifyDto();
				certificateCompanyNotifyDto.setCompanyId(certificateAllocationList.get(i).getCompany().getCompanyId());
				certificateCompanyNotifyDto.setCompanyName(certificateAllocationList.get(i).getCompany().getCompanyName());
				certificateCompanyNotifyDto.setTotalCertificate(certificateAllocationList.get(i).getAllocatedCertificates());
				certificateCompanyNotifyDto.setAllocationId(certificateAllocationList.get(i).getAllocationId());
				certificateCompanyNotifyDtoList.add(certificateCompanyNotifyDto);
			}

			  response.setStatus(HttpStatus.OK.value());
			  response.setMessage(InsuranceCompanyConstants.COMPANY_CERTIFICATE_ALLOCATE_NOTIFY);
			  response.setData(certificateCompanyNotifyDtoList);

		}else {

			  response.setStatus(HttpStatus.OK.value());
			  response.setMessage(InsuranceCompanyConstants.NO_NOTIFICATIONS);
		}
		return response;
	}
	
	public List<String> setAndGetOfflineCertificate(InsuranceCompany companyData, CompanyUserType companyUserType,
			Integer allocateCount) {

		Long serialData = certificateSerialRepo.getCountCertSerialOrder();
		serialData = serialData == null ? 0 : serialData;
		CertificateSerialNum certificateSerialNum = null;
		List<String> listOfCertificateSerialNo = new ArrayList<String>();
		for (int i = 0; i < allocateCount; i++) {

			certificateSerialNum = new CertificateSerialNum();
			++serialData;
			String serialNumber = "C-" + serialData;
			certificateSerialNum.setCompany(companyData);
			certificateSerialNum.setSerialNum(serialNumber);
			certificateSerialNum.setSerialNumOrder(serialData);
			certificateSerialNum.setIssuedStatus("offline");
			certificateSerialNum.setAllocatedDate(LocalDate.now());
			certificateSerialRepo.save(certificateSerialNum);
			listOfCertificateSerialNo.add(serialNumber);
		}
		return listOfCertificateSerialNo;
	}

	public Response<String> approvalCertificate(CertificateCompanyNotifyDto certificateCompanyNotifyDto,
			Integer userId) {
		Response<String> response = new  Response<String>();
		InsuranceCompany companyData = companyRepo.findByCompanyId(certificateCompanyNotifyDto.getCompanyId());
		if(companyData == null)
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
		
		CertificateAllocation certificateAllocation = certificateAllocationRepo.findByAllocationId(certificateCompanyNotifyDto.getAllocationId());
		certificateAllocation.setApprovedBy(userId);
		certificateAllocation.setApprovedDate(LocalDateTime.now());
		certificateAllocation.setShowCompanyNonify(0);
		certificateAllocation.setCertificateNumStatus(1);
		certificateAllocation = certificateAllocationRepo.save(certificateAllocation);
		//addNewCertificate(certificateCompanyNotifyDto);
		response.setStatus(HttpStatus.OK.value());
		response.setData(companyData.getCompanyName());
		response.setMessage(InsuranceCompanyConstants.COMPANY_CERTIFICATE_APPROVED);
		return response;
	}
	/*
	public void addNewCertificate(CertificateCompanyNotifyDto certificateCompanyNotifyDto) {
		InsuranceCompany companyData = companyRepo.findByCompanyId(certificateCompanyNotifyDto.getCompanyId());
		 for(int i=0; i<certificateCompanyNotifyDto.getTotalCertificate(); i++) {
			  Long  serialData = certificateSerialRepo.getCountCertSerialOrder();
			   CertificateSerialNum certificateSerialNum = new CertificateSerialNum();
			   if(serialData == null ) {
				   certificateSerialNum.setSerialNum("C-"+1);
				   certificateSerialNum.setCompany(companyData);
				   certificateSerialNum.setSerialNumOrder((long) 1);
				   certificateSerialNum.setAllocatedDate(LocalDate.now());
				   certificateSerialNum.setIssuedStatus("N");
					 certificateSerialRepo.save(certificateSerialNum);
			   }else {
				   serialData = serialData+1;
			  certificateSerialNum.setCompany(companyData);
			  certificateSerialNum.setSerialNum("C-"+serialData);
			  certificateSerialNum.setSerialNumOrder(serialData);
			  certificateSerialNum.setIssuedStatus("N");
			  certificateSerialNum.setAllocatedDate(LocalDate.now());
			  certificateSerialRepo.save(certificateSerialNum);
			  
			   }
		  }
		  
		}
*/
	public Response<Set<CompanyUserTypesDto>> getAllCompanyUserTypes() {
		Response<Set<CompanyUserTypesDto>> response = new Response<>();	
		Set<CompanyUserTypesDto> companyUserTypesList = new HashSet<>();
		CompanyUserTypesDto companyUserTypesDto = null ; 
		List<CompanyUserType> companyUserTypeData =  companyUserTypeRepo.findAll();
		for(CompanyUserType companyUserType : companyUserTypeData) {
			companyUserTypesDto = new CompanyUserTypesDto();
			companyUserTypesDto.setUserTypeId(companyUserType.getUserTypeId());
			companyUserTypesDto.setUserType(companyUserType.getUserType());
			companyUserTypesList.add(companyUserTypesDto);
		}
		response.setStatus(HttpStatus.OK.value());
		response.setData(companyUserTypesList);
		response.setMessage(InsuranceCompanyConstants.COMPANY_USER_TYPES);
		return response;
	}

	public Response<Integer> rejectCompanyNotiifyCertificates(Integer allocationId, Integer companyId, Integer userId) {
		Response<Integer> response = new Response();
		InsuranceCompany companyData = companyRepo.findByCompanyId(companyId);
		if(companyData == null) {
			response.setStatus(200);
			response.setMessage(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
			return response;
		}
		
		CertificateAllocation certificateAllocation = certificateAllocationRepo.findByAllocationIdAndShowCompanyNonifyAndCompany(allocationId,1,companyData);
		if(certificateAllocation == null) {
			response.setStatus(200);
			response.setMessage(InsuranceCompanyConstants.INVALID_ALLOCATION_ID);
		}else {
			certificateAllocation.setCuIsrejected(1);
			certificateAllocation.setCuRejectedAt(LocalDateTime.now());
			certificateAllocation.setCuRejectedBy(userId);
			certificateAllocation.setShowCompanyNonify(0);
			certificateAllocationRepo.save(certificateAllocation);
			response.setStatus(200);
			response.setMessage(InsuranceCompanyConstants.AUTHORITY_NOTIFICATION_REJECTED);
		}
		return response;
	}

	public Response<String> addCompanyDetails(CompanyAddDto companyAddDto) {

		Response<String> response = new Response<String>();

		

		if (companyAddDto.getCompanyId() == null) {
			addNewCompany(companyAddDto);
			response.setStatus(HttpStatus.OK.value());
			response.setData(companyAddDto.getCompanyName());
			response.setMessage(InsuranceCompanyConstants.COMPANY_SUCCESSFULLY_CREATED);
		} else {
			
			InsuranceCompany company = companyRepo.findByCompanyCode(companyAddDto.getCompanyCode());

			if (!ObjectUtils.isEmpty(company)) {

				response.setStatus(200);
				response.setMessage(InsuranceCompanyConstants.COMPANY_CODE_INVALID);
				return response;
			}
			InsuranceCompany companyData = companyRepo.findByCompanyId(companyAddDto.getCompanyId());
			companyData.setCompanyName(companyAddDto.getCompanyName());
			companyData.setCompanyCode(companyAddDto.getCompanyCode());
			companyData.setAddress1(companyAddDto.getAddress1());
			companyData.setAddress2(companyAddDto.getAddress2());
			companyData.setCity(companyAddDto.getCity());
			companyData.setContactNo(companyAddDto.getContactNumber());
			companyData.setCountry(companyAddDto.getCountry());
			companyData.setZipCode(companyAddDto.getPostBoxCode());
			companyData.setState(companyAddDto.getState());
			companyData.setMailId(companyAddDto.getMailId());

			companyData.setCreatedBy(companyAddDto.getUserId());
			companyData.setCreatedDate(LocalDateTime.now());
			// companyData.setCompanyLogo(Base64.getDecoder().decode(companyAddDto.getCompanyLogo()));
			companyData.setCompanyLogo(companyAddDto.getCompanyLogo());
			companyData = companyRepo.save(companyData);
			BranchMaster branchMaster = null;
			CompanyBranch companyBranch = null;
			if(companyAddDto.getBranchName().contains(",")) {
				String[] branches = companyAddDto.getBranchName().split(",");
				for(int i=0; i<branches.length; i++) {
					BranchMaster branchMasterData = branchMasterRepo.findByBranchName(branches[i]);
					if(branchMasterData == null) {
					branchMaster = new BranchMaster();
					branchMaster.setBranchName(branches[i]);
					branchMaster.setStatus(0);
					branchMaster.setCreatedDate(LocalDateTime.now());
					branchMaster.setCreatedBy(companyAddDto.getUserId());
					branchMaster = branchMasterRepo.save(branchMaster);
					companyBranch = new CompanyBranch();
					companyBranch.setCompany(companyData);
					companyBranch.setBranch(branchMaster);
					companyBranch.setCreatedBy(companyAddDto.getUserId());
					companyBranch.setCreatedDate(LocalDateTime.now());
					companyBranchRepo.save(companyBranch);
				}
				}
			}else {
				BranchMaster branchMasterData = branchMasterRepo.findByBranchName(companyAddDto.getBranchName());
				if(branchMasterData == null) {
				branchMaster = new BranchMaster();
				branchMaster.setBranchName(companyAddDto.getBranchName());
				branchMaster.setStatus(0);
				branchMaster.setCreatedDate(LocalDateTime.now());
				branchMaster.setCreatedBy(companyAddDto.getUserId());
				branchMaster = branchMasterRepo.save(branchMaster);
				companyBranch = new CompanyBranch();
				companyBranch.setCompany(companyData);
				companyBranch.setBranch(branchMaster);
				companyBranch.setCreatedBy(companyAddDto.getUserId());
				companyBranch.setCreatedDate(LocalDateTime.now());
				companyBranchRepo.save(companyBranch);
			}
		}
			//branchMasterRepo
			
			response.setStatus(HttpStatus.OK.value());
			response.setData(companyAddDto.getCompanyName());
			response.setMessage(InsuranceCompanyConstants.COMPANY_SUCCESSFULLY_UPDATED);
		}

		return response;
	}

	private void addNewCompany(CompanyAddDto companyAddDto) {

		// InsuranceCompany newCompany = new InsuranceCompany();
		InsuranceCompany newCompany = modelMapper.map(companyAddDto, InsuranceCompany.class);

		newCompany.setMailId(companyAddDto.getMailId());
		if (!ObjectUtils.isEmpty(companyAddDto.getCompanyLogo())) {
			// newCompany.setCompanyLogo(Base64.getDecoder().decode(companyAddDto.getCompanyLogo()));
			newCompany.setCompanyLogo(companyAddDto.getCompanyLogo());
		}
		newCompany.setContactNo(companyAddDto.getContactNumber());
		newCompany.setZipCode(companyAddDto.getPostBoxCode());

		newCompany.setCreatedBy(1);
		newCompany.setStatus("A");
		newCompany.setCreatedDate(LocalDateTime.now());
		newCompany.setAutoApprovalLimit(0);
		newCompany = companyRepo.save(newCompany);
		BranchMaster branchMaster = null;
		CompanyBranch companyBranch = null;
		if(companyAddDto.getBranchName().contains(",")) {
			String[] branches = companyAddDto.getBranchName().split(",");
			for(int i=0; i<branches.length; i++) {
				BranchMaster branchMasterData = branchMasterRepo.findByBranchName(branches[i]);
				if(branchMasterData == null) {
				branchMaster = new BranchMaster();
				branchMaster.setBranchName(branches[i]);
				branchMaster.setStatus(0);
				branchMaster.setCreatedDate(LocalDateTime.now());
				branchMaster.setCreatedBy(companyAddDto.getUserId());
				branchMaster = branchMasterRepo.save(branchMaster);
				companyBranch = new CompanyBranch();
				companyBranch.setCompany(newCompany);
				companyBranch.setBranch(branchMaster);
				companyBranch.setCreatedBy(companyAddDto.getUserId());
				companyBranch.setCreatedDate(LocalDateTime.now());
				companyBranchRepo.save(companyBranch);
			}
		}
		}else {
			BranchMaster branchMasterData = branchMasterRepo.findByBranchName(companyAddDto.getBranchName());
			if(branchMasterData == null) {
			branchMaster = new BranchMaster();
			branchMaster.setBranchName(companyAddDto.getBranchName());
			branchMaster.setStatus(0);
			branchMaster.setCreatedDate(LocalDateTime.now());
			branchMaster.setCreatedBy(companyAddDto.getUserId());
			branchMaster = branchMasterRepo.save(branchMaster);
			companyBranch = new CompanyBranch();
			companyBranch.setCompany(newCompany);
			companyBranch.setBranch(branchMaster);
			companyBranch.setCreatedBy(companyAddDto.getUserId());
			companyBranch.setCreatedDate(LocalDateTime.now());
			companyBranchRepo.save(companyBranch);
		}
	}
	}

	public Response<String> addCompanyBranchDetails(CompanyBranchAddDto companyBranchAddDto) {
		
		Response<String> response = new Response();
		InsuranceCompany companyData = companyRepo.findByCompanyId(companyBranchAddDto.getCompanyId());	
		if(companyData == null) {
			response.setMessage(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
			response.setStatus(200);
			return response;
		}
		
		String[] branches = companyBranchAddDto.getBranchName().split(",");
		List<BranchMaster> branchMasterList = branchMasterRepo.findByCompany(companyData);
		HashSet<String> branchSet = new HashSet<>();
		for (String branch : branches) {
			for (BranchMaster branchMaser : branchMasterList) {
				if (branchMaser.getBranchName().equalsIgnoreCase(branch)) {
					response.setMessage(InsuranceCompanyConstants.DUPLICATE_BRANCH);
					response.setStatus(500);
					return response;
				}
			}
		}
		BranchMaster branchMaster = null;
		CompanyBranch companyBranch = null;
		if(companyBranchAddDto.getBranchName().contains(",")) {
			for(int i=0; i<branches.length; i++) {
				BranchMaster branchMasterData = branchMasterRepo.findByBranchNameAndCompany(branches[i],companyData);
				if (branchMasterData == null) {
				branchMaster = new BranchMaster();
				branchMaster.setBranchName(branches[i]);
				branchMaster.setCompany(companyData);
				branchMaster.setStatus(0);
				branchMaster.setCreatedDate(LocalDateTime.now());
				branchMaster.setCreatedBy(companyBranchAddDto.getUserId());
				branchMaster = branchMasterRepo.save(branchMaster);
				companyBranch = new CompanyBranch();
				companyBranch.setCompany(companyData);
				companyBranch.setBranch(branchMaster);
				companyBranch.setCreatedBy(companyBranchAddDto.getUserId());
				companyBranch.setCreatedDate(LocalDateTime.now());
				companyBranch = companyBranchRepo.save(companyBranch);
				
				if(companyBranch != null) {
					CompanyUserBranch  companyUserBranch = null;
		           User user = userRepo.findByUserId(companyBranchAddDto.getUserId());
		             companyUserBranch = new CompanyUserBranch();
		             companyUserBranch.setUser(user);
		             companyUserBranch.setBranch(companyBranch.getBranch());
		             companyUserBranch.setCreatedBy(user.getUserId());
		             companyUserBranch.setCreatedDate(LocalDateTime.now());
		             companyUserBranch.setPrimaryBranch("N");
		          
		             companyUserBranch.setCompanyBranch(companyBranch);
		            companyUserBranchRepo.save(companyUserBranch);
		            }
					
			}
			}
		}else {
			BranchMaster branchMasterData = branchMasterRepo.findByBranchNameAndCompany(companyBranchAddDto.getBranchName(),companyData);
			if (branchMasterData == null) {
			branchMaster = new BranchMaster();
			branchMaster.setBranchName(companyBranchAddDto.getBranchName());
			branchMaster.setCompany(companyData);
			branchMaster.setStatus(0);
			branchMaster.setCreatedDate(LocalDateTime.now());
			branchMaster.setCreatedBy(companyBranchAddDto.getUserId());
			branchMaster = branchMasterRepo.save(branchMaster);
			companyBranch = new CompanyBranch();
			companyBranch.setCompany(companyData);
			companyBranch.setBranch(branchMaster);
			companyBranch.setCreatedBy(companyBranchAddDto.getUserId());
			companyBranch.setCreatedDate(LocalDateTime.now());
			companyBranch = companyBranchRepo.save(companyBranch);
			
			if(companyBranch != null) {
				CompanyUserBranch  companyUserBranch = null;
	           User user = userRepo.findByUserId(companyBranchAddDto.getUserId());
	             companyUserBranch = new CompanyUserBranch();
	             companyUserBranch.setUser(user);
	             companyUserBranch.setBranch(companyBranch.getBranch());
	             companyUserBranch.setCreatedBy(user.getUserId());
	             companyUserBranch.setCreatedDate(LocalDateTime.now());
	             companyUserBranch.setPrimaryBranch("N");
	          
	             companyUserBranch.setCompanyBranch(companyBranch);
	            companyUserBranchRepo.save(companyUserBranch);
	            }
		}
	}
		
		response.setMessage(InsuranceCompanyConstants.BRANCH_SUCCESSFULLY_UPDATED);
		response.setStatus(200);
		return response;
	}

}
