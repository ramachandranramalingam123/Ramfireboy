package com.beyontec.mdcp.company.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.beyontec.mdcp.company.dto.CertificateAllocationReqDto;
import com.beyontec.mdcp.company.dto.CertificateDTO;
import com.beyontec.mdcp.company.dto.CertificateDetailsDTO;
import com.beyontec.mdcp.company.dto.CertificateImageDTO;
import com.beyontec.mdcp.company.dto.CertificateIssuanceDto;
import com.beyontec.mdcp.company.dto.CertificateIssueDto;
import com.beyontec.mdcp.company.dto.CertificateRevokeDto;
import com.beyontec.mdcp.company.dto.CertificateUserTypeAllocationDto;
import com.beyontec.mdcp.company.dto.CountDTO;
import com.beyontec.mdcp.company.dto.InternalAllocationDataDto;
import com.beyontec.mdcp.company.dto.InternalAllocationDto;
import com.beyontec.mdcp.company.dto.IssuedCertificatesDetails;
import com.beyontec.mdcp.company.dto.RevokedReasonsDto;
import com.beyontec.mdcp.company.dto.SendMailDto;
import com.beyontec.mdcp.company.dto.TotalCertificateDto;
import com.beyontec.mdcp.company.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.company.model.BranchMaster;
import com.beyontec.mdcp.company.model.CertificateAllocation;
import com.beyontec.mdcp.company.model.CertificateDetails;
import com.beyontec.mdcp.company.model.CertificateRevoke;
import com.beyontec.mdcp.company.model.CertificateSerialNum;
import com.beyontec.mdcp.company.model.CompanyBranch;
import com.beyontec.mdcp.company.model.CompanyUserType;
import com.beyontec.mdcp.company.model.InsuranceCompany;
import com.beyontec.mdcp.company.model.InternalCertificate;
import com.beyontec.mdcp.company.model.RevokeReason;
import com.beyontec.mdcp.company.model.Roles;
import com.beyontec.mdcp.company.model.RolesMaster;
import com.beyontec.mdcp.company.model.RolesModule;
import com.beyontec.mdcp.company.model.User;
import com.beyontec.mdcp.company.repo.BranchMasterRepo;
import com.beyontec.mdcp.company.repo.CertificateAllocationRepo;
import com.beyontec.mdcp.company.repo.CertificateRepo;
import com.beyontec.mdcp.company.repo.CertificateRevokeRepo;
import com.beyontec.mdcp.company.repo.CertificateSerialRepo;
import com.beyontec.mdcp.company.repo.CompanyBranchRepo;
import com.beyontec.mdcp.company.repo.CompanyRepo;
import com.beyontec.mdcp.company.repo.CompanyUserTypeRepo;
import com.beyontec.mdcp.company.repo.InternalCertificateRepo;
import com.beyontec.mdcp.company.repo.RevokeReasonRepo;
import com.beyontec.mdcp.company.repo.RolesModulesRepo;
import com.beyontec.mdcp.company.repo.RolesRepo;
import com.beyontec.mdcp.company.repo.UserRepo;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.util.CertificateIssuesUtil;
import com.beyontec.mdcp.company.util.CertificateStatus;
import com.beyontec.mdcp.company.util.DateFormatUtils;
import com.beyontec.mdcp.company.util.GenerateExcelFile;
import com.beyontec.mdcp.company.util.HandlebarTemplateLoader;
import com.beyontec.mdcp.company.util.InsuranceCompanyConstants;
import com.beyontec.mdcp.company.util.QRCodeGenerator;
import com.github.jknack.handlebars.Template;
import com.google.zxing.WriterException;

@Service
public class CertificateService {

	@Autowired
	private InternalCertificateRepo internalCertificateRepo;

	@Autowired
	private CompanyRepo companyRepo;

	@Autowired
	private CertificateAllocationRepo certificateAllocationRepo;

	@Autowired
	private CertificateRepo certificateRepo;

	@Autowired
	private CertificateSerialRepo certificateSerialRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private HandlebarTemplateLoader templateLoader;

	@Autowired
	private SendMailService sendMailService;

	@Autowired
	private CertificateIssuesUtil certificateIssuesUtil;

	@Autowired
	private CertificateRevokeRepo certificateRevokeRepo;

	@Autowired
	private RevokeReasonRepo revokeReasonRepo;

	@Autowired
	private CompanyUserTypeRepo companyUserTypeRepo;

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private CompanyBranchRepo companyBranchRepo;
	
	@Autowired
	private BranchMasterRepo branchMasterRepo;
	
	@Autowired
	private DocumentService documentService;
	
	@Autowired
	private RolesModulesRepo rolesModulersRepo;
	
	@Autowired
	private RolesRepo rolesRepo;
		
    @PersistenceContext
	private EntityManager em;
	 
	@Autowired
	private FTPService fTPService;
	
	@Autowired
	private CompanyService companyService;
		
	public Response<String> addinternalAllocation(InternalAllocationDataDto internalAllocationDataDto) {
		Response<String> response = new Response<String>();
		List<InternalAllocationDto> internalAllocationDto = internalAllocationDataDto.getInternalAllocations();
		InsuranceCompany companyData = companyRepo.findByCompanyId(internalAllocationDto.get(0).getCompanyId());
		Integer allocatedCount = certificateAllocationRepo.getSumOfAllocatedCertificate(companyData);
		if (companyData == null) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
			return response;
		}

		if (allocatedCount == null) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.NO_AVAILABLE_CERTIFICATE);
			return response;
		}

		Integer inernalAllocation = internalCertificateRepo.getSumOfAllocatedCertificateByAll(companyData);
		if (inernalAllocation != null) {
			allocatedCount = allocatedCount - inernalAllocation;
		}
		Integer totalInternalAllocate = 0;
		for (InternalAllocationDto allocate : internalAllocationDto) {
			totalInternalAllocate = totalInternalAllocate + allocate.getAllocateCount();
		}
		int x = 1;
		for (InternalAllocationDto allocateData : internalAllocationDto) {
			CompanyUserType companyUserType = companyUserTypeRepo.findByUserTypeId(allocateData.getUserTypeId());
			if (allocatedCount < totalInternalAllocate) {
				response.setStatus(HttpStatus.OK.value());
				response.setMessage(InsuranceCompanyConstants.AVAILABLE_CERTIFICATE_LOW);
				return response;
		
			} 
			if(allocateData.getAllocateCount() == 0)
				continue;
			
			if(companyUserType.getIsOffline() == 1){
				response = invokeOfflineCertificateInternalAllocation(companyData, companyUserType, allocateData);
				return response;
			} else {
				InternalCertificate internalCertificate = new InternalCertificate();
				internalCertificate.setAllocatedDate(LocalDateTime.now());
				internalCertificate.setAllocatedCertificates(allocateData.getAllocateCount());
				internalCertificate.setCompanyUserType(companyUserType);
				internalCertificate.setCompany(companyData);
				internalCertificateRepo.save(internalCertificate);
			}
		}
		response.setStatus(HttpStatus.OK.value());
		response.setData(companyData.getCompanyName());
		response.setMessage(InsuranceCompanyConstants.SUCCESSFULLY_CERTIFICATE_ALLOCATED);
		return response;

	}
	
	private Response<String> invokeOfflineCertificateInternalAllocation(InsuranceCompany companyData,
			CompanyUserType companyUserType, InternalAllocationDto allocateData) {

		Response<String> response = new Response<>();
		try {
			List<String> listOfOfflineCertificates = companyService.setAndGetOfflineCertificate(companyData,
					companyUserType, allocateData.getAllocateCount());
			new GenerateExcelFile().createOfflineCertificateXlsx(listOfOfflineCertificates, companyData.getCompanyName());

			response.setStatus(HttpStatus.OK.value());
			response.setData(companyData.getCompanyName());
			response.setMessage(InsuranceCompanyConstants.OFFLINE_CERTIFICATE_SUCCESSFULLY_ALLOCATED);

		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setData(companyData.getCompanyName());
			response.setMessage(InsuranceCompanyConstants.OFFLINE_CERTIFICATE_ALLOCATION_FAILED + e.getMessage());
		}
		return response;
	}

	public Response<String> addSingleAllocation(InternalAllocationDto internalAllocationDto) {
		Response<String> response = new Response<String>();
		InsuranceCompany companyData = companyRepo.findByCompanyId(internalAllocationDto.getCompanyId());
		if (companyData == null) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
			return response;
		}
		CompanyUserType companyUserType = companyUserTypeRepo.findByUserTypeId(internalAllocationDto.getUserTypeId());
		if (companyUserType == null) {
			response.setStatus(HttpStatus.OK.value());
			response.setData(companyData.getCompanyName());
			response.setMessage(InsuranceCompanyConstants.INVALID_USER_TYPE);
			return response;
		}
		Integer allocatedCount = certificateAllocationRepo.getSumOfAllocatedCertificate(companyData);

		if (allocatedCount == null) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.NO_AVAILABLE_CERTIFICATE);
			return response;
		}
		Integer inernalAllocation = internalCertificateRepo.getSumOfAllocatedCertificateByAll(companyData);
		if (inernalAllocation != null) {
			allocatedCount = allocatedCount - inernalAllocation;
		}
		if (allocatedCount < internalAllocationDto.getAllocateCount()) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.AVAILABLE_CERTIFICATE_LOW);
			return response;
		}
		InternalCertificate internalCertificate = new InternalCertificate();
		internalCertificate.setAllocatedDate(LocalDateTime.now());
		internalCertificate.setAllocatedCertificates(internalAllocationDto.getAllocateCount());
		internalCertificate.setCompanyUserType(companyUserType);
		internalCertificate.setCompany(companyData);
		internalCertificateRepo.save(internalCertificate);
		response.setStatus(HttpStatus.OK.value());
		response.setData(companyData.getCompanyName());
		response.setMessage(InsuranceCompanyConstants.SUCCESSFULLY_CERTIFICATE_ALLOCATED);
		return response;
	}

	public Response<String> createCertificateReq(CertificateAllocationReqDto certificateAllocationReqDto,
			Integer userId, MultipartFile paymentDoc, String fileType, String paymentDescription) throws IOException {
		Response<String> response = new Response<String>();
		InsuranceCompany companyData = companyRepo.findByCompanyId(certificateAllocationReqDto.getCompanyId());
		if (companyData == null)
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
		CertificateAllocation certificateAllocation = new CertificateAllocation();
		certificateAllocation.setRequestedBy(userId);
		certificateAllocation.setRequestedDate(LocalDateTime.now());
		certificateAllocation.setRequestedCertificates(certificateAllocationReqDto.getCertificateReqCount());
		certificateAllocation.setShowAuthorityNonify(1);
		certificateAllocation.setIsrejected(0);
		certificateAllocation.setCuIsrejected(0);
		certificateAllocation.setCompany(companyData);

		certificateAllocation = certificateAllocationRepo.save(certificateAllocation);
		if(fileType != null && paymentDescription != null) {
			certificateAllocation.setFileName("PY-"+certificateAllocation.getAllocationId());
			certificateAllocation.setPaymentFileType(fileType);
			certificateAllocation.setPaymentDescription(paymentDescription);
			fTPService.uploadFTPFile("PY-"+certificateAllocation.getAllocationId()+"."+fileType, paymentDoc.getInputStream());
			certificateAllocationRepo.save(certificateAllocation);
			}
		
		List<String> mailList = new ArrayList<>();
		RolesModule rolesModule = rolesModulersRepo.findByPortalAndModuleLabel("IA", "Receive Mail");
		
		List<Roles> roles = rolesRepo.findByRolesModule(rolesModule);
		for (Roles role : roles) {
			List<User> users = userRepo.findByRoleId(role.getRolesMaster().getMasterId());
			
			users.forEach(user -> {
				mailList.add(user.getEmail());
			});
		}
		
		if (!mailList.isEmpty()) {
			Map<String, Object> data = new HashMap<>();
			data.put("company", companyData.getCompanyName());
			data.put("count", certificateAllocationReqDto.getCertificateReqCount());

			String htmlContent = null;
			try {
				Template template = templateLoader.getTemplate("certificateRequest");
				htmlContent = template.apply(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			SendMailDto mailDto = new SendMailDto();
			mailDto.setSubject("MDCP - Certificate Request " + companyData.getCompanyName());
			mailDto.setMessage(htmlContent);
			mailDto.setToEmail(mailList);
			mailDto.setDisplayEmailSignature(true);
			try {
				sendMailService.sendEmail(mailDto);
			} catch (Exception e) {
				
				response.setStatus(HttpStatus.OK.value());
				response.setData(companyData.getCompanyName());
				response.setMessage(InsuranceCompanyConstants.REQUEST_SUCCESSFULLY_CREATED);
				return response;
			}
		}

		response.setStatus(HttpStatus.OK.value());
		response.setData(companyData.getCompanyName());
		response.setMessage(InsuranceCompanyConstants.REQUEST_SUCCESSFULLY_CREATED);
		return response;
	}


	public Response<CertificateDTO> getCertificateDetailsByCompany(Integer companyId, int pageSize, int currentPage,
			String filter, Integer issuedBy, String firstDate, String endDate, Integer branchId, String value) {

		Response<CertificateDTO> response = new Response<>();
		CertificateDTO dashboardDTO = new CertificateDTO();
		List<IssuedCertificatesDetails> issuedCertificatesDetailsList = new ArrayList<>();

		Pageable pagable = PageRequest.of(currentPage, pageSize);

		InsuranceCompany insuranceCompany = companyRepo.findByCompanyId(companyId);
		if (insuranceCompany == null)
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
		
		
		if(!certificateRepo.existsByInsuranceCompany(insuranceCompany)) {
			response.setMessage(InsuranceCompanyConstants.NOT_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
			return response;
		}

		CompanyUserType companyUserType = companyUserTypeRepo.findByUserTypeId(issuedBy);
		List<Integer> userList = userRepo.findByUserIdAndCompany(insuranceCompany.getCompanyId(), companyUserType.getUserTypeId());

		List<CertificateDetails> certificateDetails = null;
		int totalCertificatesCount = 0;
		
		if (!StringUtils.isEmpty(filter)) {
			if(branchId != null) {
			CompanyBranch companyBranch = companyBranchRepo.findByCompanyBranchId(branchId);
			if(companyBranch != null && companyBranch.getBranch() != null) {
			certificateDetails = certificateRepo
					.findAllByInsuranceCompanyAndBranchAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
							insuranceCompany,companyBranch.getBranch(), userList, convertToLocalDateTimeViaInstant(getStartDate(filter)),
							LocalDateTime.now());
			
			totalCertificatesCount = certificateRepo
			.findAllByInsuranceCompanyAndBranchAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
					insuranceCompany,companyBranch.getBranch(), userList, convertToLocalDateTimeViaInstant(getStartDate(filter)),
					LocalDateTime.now()).size();
			}else {
				certificateDetails = certificateRepo
						.findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
								insuranceCompany, userList, convertToLocalDateTimeViaInstant(getStartDate(filter)),
								LocalDateTime.now());
				
				totalCertificatesCount = certificateRepo
				.findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
						insuranceCompany, userList, convertToLocalDateTimeViaInstant(getStartDate(filter)),
						LocalDateTime.now()).size();
			}
		}else {
			certificateDetails = certificateRepo
					.findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
							insuranceCompany, userList, convertToLocalDateTimeViaInstant(getStartDate(filter)),
							LocalDateTime.now());
			
			totalCertificatesCount = certificateRepo
			.findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
					insuranceCompany, userList, convertToLocalDateTimeViaInstant(getStartDate(filter)),
					LocalDateTime.now()).size();
		}
		} else {
			if(branchId != null) {
				CompanyBranch companyBranch = companyBranchRepo.findByCompanyBranchId(branchId);
				if(companyBranch != null && companyBranch.getBranch() != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			certificateDetails = certificateRepo
					.findAllByInsuranceCompanyAndBranchAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
							insuranceCompany,companyBranch.getBranch(), userList, LocalDateTime.parse(firstDate, formatter),
							LocalDateTime.parse(endDate, formatter));

		}else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			certificateDetails = certificateRepo
					.findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
							insuranceCompany, userList, LocalDateTime.parse(firstDate, formatter),
							LocalDateTime.parse(endDate, formatter));
		}
			}else {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				certificateDetails = certificateRepo
						.findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
								insuranceCompany, userList, LocalDateTime.parse(firstDate, formatter),
								LocalDateTime.parse(endDate, formatter));
			}
		}
			
		if (certificateDetails == null) {
			response.setMessage(InsuranceCompanyConstants.NOT_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
		} else {

			for (CertificateDetails certificateDetail : certificateDetails) {
				IssuedCertificatesDetails issuedCertificatesDetails = new IssuedCertificatesDetails();
				issuedCertificatesDetails.setCertificateSerialNumber(certificateDetail.getCertificateSerialNumber());
				issuedCertificatesDetails.setInsured(certificateDetail.getInsured());
				issuedCertificatesDetails.setPolicyNumber(certificateDetail.getPolicyNumber());
				if (certificateDetail.getStatus() == 0) {
					issuedCertificatesDetails.setStatus(CertificateStatus.revoked.name());
				} else {
					issuedCertificatesDetails.setStatus(CertificateStatus.issued.name());
				}
				User user = userRepo.findByUserId(certificateDetail.getUploadedBy());
				issuedCertificatesDetails.setExpiryDate(certificateDetail.getExpiryDate());
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
				issuedCertificatesDetails.setCommencingDate(formatter.format(certificateDetail.getCommencingDate()));

				SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm a");
				issuedCertificatesDetails.setCommencingTime(formatterTime.format(certificateDetail.getCommencingDate()));

				issuedCertificatesDetails.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
				issuedCertificatesDetails.setRegistrationNo(certificateDetail.getRegistartionNumber());
				issuedCertificatesDetails.setChassisNo(certificateDetail.getChassisNumber());
				issuedCertificatesDetails.setMarkType(certificateDetail.getVechicleType());
				issuedCertificatesDetails.setQrCode(
						"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetail.getQrCode()));
				issuedCertificatesDetails.setLicensed(certificateDetail.getLicensed());
				issuedCertificatesDetails.setUsage(certificateDetail.getUsage());
				if (!StringUtils.isEmpty(user.getSignature())) {
					issuedCertificatesDetails.setSignature(
							"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getSignature()));
				}
				issuedCertificatesDetailsList.add(issuedCertificatesDetails);

			}
			issuedCertificatesDetailsList = issuedCertificatesDetailsList.stream()
					.filter(detail -> org.apache.commons.lang.StringUtils
							.containsIgnoreCase(detail.getCertificateSerialNumber(), value)
							|| org.apache.commons.lang.StringUtils.containsIgnoreCase(detail.getInsured(), value)
							|| org.apache.commons.lang.StringUtils.containsIgnoreCase(detail.getPolicyNumber(), value)
							|| org.apache.commons.lang.StringUtils.containsIgnoreCase(detail.getUsage(), value)
							|| org.apache.commons.lang.StringUtils.containsIgnoreCase(detail.getStatus(), value)
							|| org.apache.commons.lang.StringUtils.containsIgnoreCase(detail.getRegistrationNo(), value))
					.collect(Collectors.toList());
			dashboardDTO.setTotalCertificates(issuedCertificatesDetailsList.size());
			if (!StringUtils.isEmpty(pageSize) && !StringUtils.isEmpty(currentPage)) {
				int toIndex = currentPage * pageSize + pageSize;
				if (toIndex > issuedCertificatesDetailsList.size()) {
					toIndex = issuedCertificatesDetailsList.size();
				}
				issuedCertificatesDetailsList = issuedCertificatesDetailsList.subList(currentPage * 5, toIndex);
			}
			dashboardDTO.setIssuedCertificatesDetails(issuedCertificatesDetailsList);
//			dashboardDTO.setTotalCertificates(
//					certificateRepo.findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
//							insuranceCompany, userList, convertToLocalDateTimeViaInstant(getStartDate(filter)),
//							LocalDateTime.now()).size());
			

			response.setData(dashboardDTO);
			response.setStatus(HttpStatus.OK.value());

		}

		return response;

	}

	public Response<CertificateUserTypeAllocationDto> getCount(Integer companyId) {

		Response<CertificateUserTypeAllocationDto> response = new Response<>();
		List<CountDTO> countDtoList = new ArrayList<>();
		InsuranceCompany insuranceCompany = companyRepo.findByCompanyId(companyId);
		if (ObjectUtils.isEmpty(insuranceCompany)) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
			return response;
		}
		TotalCertificateDto totalCertificateDto = new TotalCertificateDto();
		CertificateUserTypeAllocationDto certificateUserTypeAllocationDto = new CertificateUserTypeAllocationDto();

		List<CompanyUserType> companyUserType = companyUserTypeRepo.findAll();
		for (CompanyUserType companyUserTypeData : companyUserType) {
			Integer internalCertificateCount = internalCertificateRepo.getSumOfAllocatedCertificate(insuranceCompany,
					companyUserTypeData);
			CountDTO countDTO = new CountDTO();
			if (internalCertificateCount == null) {
				countDTO.setUserType(companyUserTypeData.getUserType());
				countDTO.setAllocatedCount(0);
				countDtoList.add(countDTO);
			} else {

				countDTO.setUserType(companyUserTypeData.getUserType());
				countDTO.setAllocatedCount(internalCertificateCount);
				countDtoList.add(countDTO);
			}

		}
		//Integer inernalAllocation = internalCertificateRepo.getSumOfAllocatedCertificateByAll(insuranceCompany);
		Integer issuedCertsCount  = certificateRepo.countByCompany(insuranceCompany);
		Integer allocatedCertificateByCompany = certificateAllocationRepo
				.getSumOfAllocatedCertificateByCompany(insuranceCompany);
		if (allocatedCertificateByCompany == null) {
			totalCertificateDto.setAvaillableCertificates(0);
			totalCertificateDto.setAllocatedCertificates(0);
		} else {
			if (issuedCertsCount != null && issuedCertsCount > 0) {
				totalCertificateDto.setAvaillableCertificates(allocatedCertificateByCompany - issuedCertsCount);
			} else {
				totalCertificateDto.setAvaillableCertificates(allocatedCertificateByCompany);
			}

			totalCertificateDto.setAllocatedCertificates(allocatedCertificateByCompany);
		}
		certificateUserTypeAllocationDto.setUserTypecertificates(countDtoList);
		certificateUserTypeAllocationDto.setTotalCertificate(totalCertificateDto);
		response.setData(certificateUserTypeAllocationDto);
		response.setStatus(HttpStatus.OK.value());

		return response;
	}

	public Response<CertificateDetailsDTO> getCertificateDetails(String certificateNo) {

		Response<CertificateDetailsDTO> response = new Response<>();
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateNo);
		if (certificateDetails == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			return response;
		}
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		CertificateDetailsDTO certificateDetailsDTO = modelMapper.map(certificateDetails, CertificateDetailsDTO.class);
		certificateDetailsDTO.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
		certificateDetailsDTO.setQrCode(
				"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
		certificateDetailsDTO.setCommencingDate(formatter.format(certificateDetails.getCommencingDate()));
		certificateDetailsDTO.setExpiryDate(formatter.format(certificateDetails.getExpiryDate()));
		
		SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm a");
		certificateDetailsDTO.setCommencingTime(formatterTime.format(certificateDetails.getCommencingDate()));
		
		if (!StringUtils.isEmpty(user.getSignature())) {
			certificateDetailsDTO.setSignature(
					"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getSignature()));
		}


		if (certificateDetails.getStatus() == 1) {
			certificateDetailsDTO.setStatus(CertificateStatus.issued.toString());
		} else {
			certificateDetailsDTO.setStatus(CertificateStatus.revoked.toString());
		}
		
		response.setData(certificateDetailsDTO);
		response.setStatus(200);
		return response;
	}

	public Response<CertificateDetailsDTO> getCertificateDetailsByRegistrationNo(String registrationNo) {

		Response<CertificateDetailsDTO> response = new Response<>();
		CertificateDetails certificateDetails = certificateRepo.findByRegistartionNumber(registrationNo);
		if (certificateDetails == null) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			response.setStatus(400);
			return response;
		}

		CertificateDetailsDTO certificateDetailsDTO = modelMapper.map(certificateDetails, CertificateDetailsDTO.class);

		if (!StringUtils.isEmpty(certificateDetails.getQrCode())) {
			certificateDetailsDTO.setQrCode(
					"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));
		}
		
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		
		if (!StringUtils.isEmpty(user.getSignature())) {
			certificateDetailsDTO.setSignature(
					"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getSignature()));
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
		certificateDetailsDTO.setCommencingDate(formatter.format(certificateDetails.getCommencingDate()));

		certificateDetailsDTO.setExpiryDate(formatter.format(certificateDetails.getExpiryDate()));
		
		SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm a");
		certificateDetailsDTO.setCommencingTime(formatterTime.format(certificateDetails.getCommencingDate()));

		certificateDetailsDTO.setCompanyName(certificateDetails.getInsuranceCompany().getCompanyName());
		response.setData(certificateDetailsDTO);
		response.setStatus(200);
		return response;
	}

	public Response<CertificateImageDTO> newCertificateIssue(CertificateIssueDto certificateIssueDto) throws ParseException {
		Response<CertificateImageDTO> response = new Response<CertificateImageDTO>();
		CertificateDetails certificateDetail = new CertificateDetails();
		InsuranceCompany insuranceCompany = null;
		if (null != certificateIssueDto.getCompanyId()) {
			insuranceCompany = companyRepo.findByCompanyId(certificateIssueDto.getCompanyId());
		}
		if (insuranceCompany == null && !StringUtils.isEmpty(certificateIssueDto.getCompanyCode())) {
			insuranceCompany = companyRepo.findByCompanyCode(certificateIssueDto.getCompanyCode());
		}
		if (insuranceCompany == null)
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
		if (!insuranceCompany.getStatus().equalsIgnoreCase("A")){
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.COMPANY_IS_INACTIVE);
			return response;
		}
		// InternalCertificate internalAlloc =
		// internalCertificateRepo.findByCompany(insuranceCompany);

		if (org.apache.commons.lang3.StringUtils.isNotEmpty(certificateIssueDto.getCertificateNo())) {
			CertificateDetails certificateSerialNumCheck = certificateRepo
					.findByCertificateSerialNumber(certificateIssueDto.getCertificateNo());
			if (certificateSerialNumCheck != null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_ALREADY_ISSUED);
				return response;
			}
		} else {
			CertificateSerialNum serialNumber = certificateSerialRepo
					.findFirstByCompanyAndIssuedStatusOrderBySerialNumOrderAsc(insuranceCompany, "N");
			if (serialNumber == null) {
				response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
				response.setStatus(HttpStatus.OK.value());
				return response;
			}
			certificateIssueDto.setCertificateNo(serialNumber.getSerialNum());
		}
		CertificateSerialNum certificateSerialNum = certificateSerialRepo
				.findBySerialNum(certificateIssueDto.getCertificateNo());
		if (ObjectUtils.isEmpty(certificateSerialNum)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.INVALID_SERIAL_NUM);
			return response;
		}
		
		/*
		 * List<CertificateDetails> certificatePolicyNumheckList = certificateRepo
				.findAllByPolicyNumber(certificateIssueDto.getPolicyNumber());
		  if (certificatePolicyNumheckList != null) {
			  for(CertificateDetails certificateDetails : certificatePolicyNumheckList) {
				  if(certificateDetails.getStatus() == 0) {
						response.setStatus(HttpStatus.BAD_REQUEST.value());
						response.setMessage(InsuranceCompanyConstants.POLICY_NO_ALREADY_REVOKED);
						return response;
				  }
			  }
		}
		 */
		
		/*CertificateDetails certificateRegisterNumheck = certificateRepo
				.findByRegistartionNumber(certificateIssueDto.getRegistrationNo());*/
		List<CertificateDetails> certificateChassisNoList = certificateRepo
				.findAllByChassisNumber(certificateIssueDto.getChassisNo());
		
		if(certificateChassisNoList != null && !certificateChassisNoList.isEmpty()) {
			boolean isDateOverlap = false;
			Date commencingDate = DateFormatUtils.getFormattedDateTime(certificateIssueDto.getCommencingDate());
			Date expiryDate = DateFormatUtils.getFormattedDateTime(certificateIssueDto.getExpiryDate());
			
			
			for(CertificateDetails certificateDtls: certificateChassisNoList) {
				if (certificateDtls.getRegistartionNumber().equals(certificateIssueDto.getRegistrationNo())) {
					
					Date existingCommencingDate = certificateDtls.getCommencingDate();
					Date existingExpiryDate = certificateDtls.getExpiryDate();
					
					if(existingCommencingDate.compareTo(commencingDate) < 0 && existingExpiryDate.compareTo(commencingDate) < 0 
							&& existingExpiryDate.compareTo(expiryDate) < 0) {
						continue;
					}else {
						if(certificateDtls.getStatus() == 0) {
							continue;
						}
						isDateOverlap = true;
					}
				}
				
				if(isDateOverlap){
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage(InsuranceCompanyConstants.CHASSIS_REGNO_OVERLAPPED);
					return response;
				}
			}
		}
		
		
//		User user = userRepo.findByUserId(certificateIssueDto.getUserId());
//		List<InternalCertificate> internalCertificate = internalCertificateRepo.getAllocatedCertificate(insuranceCompany,user.getCompanyUserType());
//		if(internalCertificate == null || internalCertificate.size() <=0) {
//			response.setStatus(HttpStatus.OK.value());
//			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
//			return response;
//		}
		certificateDetail.setCertificateSerialNumber(certificateIssueDto.getCertificateNo());
		certificateDetail.setInsured(certificateIssueDto.getPolicyHolder());
		
		User user = userRepo.findByUserId(certificateIssueDto.getUserId());
		
		if (ObjectUtils.isEmpty(user)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.USER_ID_INVALID);
			return response;
		}
		
		if(!insuranceCompany.getCompanyId().equals(user.getCompanyId())) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.INVALID_COMPANY);// Invalid company
			return response;
		}
		if (!user.getStatus().equalsIgnoreCase("A")){
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.USER_IS_INACIVE);
			return response;
		}
		RolesMaster rolesMaster = new RolesMaster();
		rolesMaster.setMasterId(user.getRoleId());
		List<Roles> roles = rolesRepo.findByRolesMaster(rolesMaster);
		if (roles == null || !roles.stream().anyMatch(r -> r.getRolesModule().getModuleId()==22)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.USER_IS_INVALID_ROLE);
			return response;			
		}
		certificateDetail.setUploadedBy(certificateIssueDto.getUserId());
		certificateDetail.setIssuedBy(certificateIssueDto.getIssuedBy());
		certificateDetail.setApprovedBy(certificateIssueDto.getApprovedBy());
		certificateDetail.setRegistartionNumber(certificateIssueDto.getRegistrationNo());
		//certificateDetail.setCommencingDate(DateFormatUtils.formatDate(certificateIssueDto.getCommencingDate()));
		
		certificateDetail.setCommencingDate(DateFormatUtils.getFormattedDateTime(certificateIssueDto.getCommencingDate()));
		certificateDetail.setExpiryDate(DateFormatUtils.formatDate(certificateIssueDto.getExpiryDate()));
		certificateDetail.setPolicyNumber(certificateIssueDto.getPolicyNumber());
		certificateDetail.setVechicleType(certificateIssueDto.getVehicleType());
		certificateDetail.setChassisNumber(certificateIssueDto.getChassisNo());
		certificateDetail.setApprovedBy(certificateIssueDto.getApprovedBy());
		certificateDetail.setLicensed(certificateIssueDto.getLicensed());
		certificateDetail.setStatus(CertificateStatus.issued.getStatus());
		certificateDetail.setIntermediaryIRA(certificateIssueDto.getIntermediaryIRA());
		certificateDetail.setIntermediary(certificateIssueDto.getIntermediary());
		certificateDetail.setSumInsured(certificateIssueDto.getSumInsured());
		certificateDetail.setUsage(certificateIssueDto.getUsage());
		certificateDetail.setCreatedDate(LocalDateTime.now());
		certificateDetail.setIssuedDate(LocalDate.now());
		String qrCodeBase64 = getQrBase(certificateIssueDto.getCertificateNo());
		certificateIssueDto.setQrCode("data:image/jpeg;base64," + qrCodeBase64);
		certificateDetail.setQrCode(Base64.getDecoder().decode(qrCodeBase64));
		certificateDetail.setInsuranceCompany(insuranceCompany);

		if (certificateIssueDto.getEmail().contains(",")) {
			String[] emailData = certificateIssueDto.getEmail().split(",");
			certificateDetail.setPrimaryEmail(emailData[0]);
			StringBuffer secondaryEmail = new StringBuffer();
			for (int i = 1; i < emailData.length; i++) {
				secondaryEmail.append(emailData[i]);
				secondaryEmail.append(",");
			}
			secondaryEmail.deleteCharAt(secondaryEmail.length() - 1);
			certificateDetail.setSecondaryEmail(secondaryEmail.toString());
		} else {
			certificateDetail.setPrimaryEmail(certificateIssueDto.getEmail());
		}
		CompanyBranch companyBranch = null;
		if (certificateIssueDto.getCompanyBranchId() != null) {
			companyBranch = companyBranchRepo.findByCompanyBranchId(certificateIssueDto.getCompanyBranchId());
			if (companyBranch != null) {
				certificateDetail.setBranch(companyBranch.getBranch());
			}
		} else if (certificateIssueDto.getBranchCode() != null) {
			BranchMaster branchMaster = branchMasterRepo.findByBranchCodeAndCompany(certificateIssueDto.getBranchCode(), insuranceCompany);

			if (branchMaster != null) {
				 certificateDetail.setBranch(branchMaster); 
			}
		}
		if (certificateDetail.getBranch( )== null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.INVALID_BRANCH);
			return response;
		}
		certificateRepo.save(certificateDetail);
		certificateSerialNum.setIssuedStatus("Y");
		certificateSerialRepo.save(certificateSerialNum);
		//internalCertificate.get(0).setAllocatedCertificates(internalCertificate.get(0).getAllocatedCertificates()-1);
		//internalCertificateRepo.save(internalCertificate.get(0));
		CertificateImageDTO certificateImageDTO = new CertificateImageDTO();
		certificateImageDTO.setCertificateNumber(certificateIssueDto.getCertificateNo());
		try {
			certificateImageDTO.setCertificateImage(documentService.certificateImageShow(certificateIssueDto.getCertificateNo()).getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			certificateIssuesUtil.showCertificateByPDF(certificateIssueDto);
		} catch (Exception e1) {
			response.setData(certificateImageDTO);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.SUCCESSFULLY_CERTIFICATES_ISSUED 
					+" " + InsuranceCompanyConstants.MAIL_QUOTA_EXCEED);
		}
		
		response.setData(certificateImageDTO);
		response.setStatus(HttpStatus.OK.value());
		response.setMessage(InsuranceCompanyConstants.SUCCESSFULLY_CERTIFICATES_ISSUED);
		return response;
	}

	public Response<String> getCertificateNumber(Integer companyId) {
		Response<String> response = new Response<String>();
		InsuranceCompany insuranceCompany = companyRepo.findByCompanyId(companyId);
		if (insuranceCompany == null)
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
		CertificateSerialNum serialNumber = certificateSerialRepo
				.findFirstByCompanyAndIssuedStatusOrderBySerialNumOrderAsc(insuranceCompany, "N");
		if (serialNumber == null) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
		} else {
			response.setData(serialNumber.getSerialNum());
			response.setMessage(InsuranceCompanyConstants.SUCCESS);
			response.setStatus(HttpStatus.OK.value());
		}
		return response;
	}

	public Response<List<CertificateIssuanceDto>> getCertificateIssuance(Integer companyId) {

		Response<List<CertificateIssuanceDto>> response = new Response<>();
		List<CertificateIssuanceDto> certificateIsssuances = new ArrayList<>();

		InsuranceCompany company = companyRepo.findByCompanyId(companyId);

		if (ObjectUtils.isEmpty(company)) {
			response.setMessage(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
			response.setStatus(200);
			return response;
		}
		if(!certificateRepo.existsByInsuranceCompany(company)) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
			return response;
		}
		List<CertificateDetails> issuedCertificates = certificateRepo.findAllByInsuranceCompanyAndStatus(company, 1);
		if (issuedCertificates == null || issuedCertificates.size()<=0) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			response.setStatus(200);
		} else {

			Map<String, Integer> userTypeCountMap = new HashMap<>();
			for (CertificateDetails certificateDetail : issuedCertificates) {
				User user = userRepo.findByUserId(certificateDetail.getUploadedBy());
				String company_UserType = getCompanyUserTypeByUserId(user.getUserTypeId());
				if (userTypeCountMap.containsKey(company_UserType)) {
					Integer count = userTypeCountMap.get(company_UserType);
					userTypeCountMap.put(company_UserType, count + 1);
				} else {
					userTypeCountMap.put(company_UserType, 1);
				}

			}

			for (String UserTypeMap : userTypeCountMap.keySet()) {
				CompanyUserType companyUserTypeData = companyUserTypeRepo.findByUserType(UserTypeMap);
				CertificateIssuanceDto Issuance = new CertificateIssuanceDto();
				Integer internalAllocationSum = internalCertificateRepo.getSumOfAllocatedCertificate(company,
						companyUserTypeData);
				Issuance.setIssuerType(companyUserTypeData.getUserType());
				Issuance.setIssuedCertificates(userTypeCountMap.get(companyUserTypeData.getUserType()));
				if (internalAllocationSum != null) {
					Issuance.setPendingCertificates(
							internalAllocationSum - userTypeCountMap.get(companyUserTypeData.getUserType()));
				} else {
					Issuance.setPendingCertificates(0);
				}
				certificateIsssuances.add(Issuance);
			}

			response.setData(certificateIsssuances);
			response.setStatus(200);
		}

		return response;
	}

	public Response<String> revokeCertificate(CertificateRevokeDto certificateRevokeDto) throws ParseException {
		Response<String> response = new Response<String>();
		User user = userRepo.findByUserId(certificateRevokeDto.getUserId());
		
		if (ObjectUtils.isEmpty(user)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.USER_ID_INVALID);
			return response;
		}
		
		if (!user.getStatus().equalsIgnoreCase("A")){
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.USER_IS_INACIVE);
			return response;
		}
		RolesMaster rolesMaster = new RolesMaster();
		rolesMaster.setMasterId(user.getRoleId());
		List<Roles> roles = rolesRepo.findByRolesMaster(rolesMaster);
		if (roles == null || !roles.stream().anyMatch(r -> r.getRolesModule().getModuleId() == 21)) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.USER_IS_INVALID_ROLE);
			return response;
		}
		RevokeReason revokeReason = revokeReasonRepo.findByReasonId(certificateRevokeDto.getReasonId());
		CertificateRevoke certificateRevoke = new CertificateRevoke();
		CertificateDetails certificateDetails = null;
		if (org.apache.commons.lang.StringUtils.isNotBlank(certificateRevokeDto.getCertificateNo())) {
			certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateRevokeDto.getCertificateNo());
		} else if (certificateDetails == null) {
			if (org.apache.commons.lang.StringUtils.isBlank(certificateRevokeDto.getRegistartionNumber())
					|| org.apache.commons.lang.StringUtils.isBlank(certificateRevokeDto.getChassisNumber())
					|| org.apache.commons.lang.StringUtils.isBlank(certificateRevokeDto.getPolicyNumber())) {
				response.setMessage(InsuranceCompanyConstants.CERTIFICATE_REVOKE_VALIDATION);
				response.setStatus(400);
				return response;
			}
			
			String query = "Select certificate from CertificateDetails certificate where certificate.registartionNumber ='"
					+ certificateRevokeDto.getRegistartionNumber() + "' AND certificate.chassisNumber ='"
					+ certificateRevokeDto.getChassisNumber()+"' AND certificate.policyNumber='" + certificateRevokeDto.getPolicyNumber()+"'";
			List<CertificateDetails> certificateDetailsList = em
					.createQuery(query, CertificateDetails.class).getResultList();
			if (CollectionUtils.isNotEmpty(certificateDetailsList)) {
				certificateDetails = certificateDetailsList.get(0);
			}
		}
		if (certificateDetails == null) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			response.setStatus(400);
			return response;
		}
		if (CertificateStatus.revoked.getStatus() == certificateDetails.getStatus()) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_ALREADY_REVOKED);
			response.setStatus(HttpStatus.OK.value());
		} else {

			certificateDetails.setStatus(CertificateStatus.revoked.getStatus());
			// certificateDetails.setRevokeReason(certificateRevokeDto.getReason());
			certificateDetails.setUpdatedBy(certificateRevokeDto.getUserId());
			certificateDetails.setExpiryDate(new Date());
			certificateDetails = certificateRepo.save(certificateDetails);
			certificateRevoke.setReason(revokeReason.getReason());
			DateFormatUtils.getFormattedDateTime(DateFormatUtils.inputSDF.format(new Date()));
			certificateRevoke.setRevokedAt(DateFormatUtils.getFormattedDateTime(DateFormatUtils.inputSDFTime.format(new Date())));
			certificateRevoke.setRevokedBy(certificateRevokeDto.getUserId());
			certificateRevoke.setCertificateDetails(certificateDetails);
			certificateRevokeRepo.save(certificateRevoke);
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_REVOKE_SUCCESS);
			response.setStatus(HttpStatus.OK.value());
			
			Map<String, Object> data = new HashMap<>();
			data.put("insured",certificateDetails.getInsured());
			data.put("certificateNo",certificateDetails.getCertificateSerialNumber());
			data.put("revokeReason",revokeReason.getReason());
			data.put("registrationNo", certificateDetails.getRegistartionNumber());

			String htmlContent = null;
			try {
				Template template = templateLoader.getTemplate("revokeCertificate");
				htmlContent = template.apply(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			SendMailDto mailDto = new SendMailDto();
			mailDto.setSubject("Certificate Revoke");
			mailDto.setMessage(htmlContent);
			mailDto.setToEmail(Arrays.asList(certificateDetails.getPrimaryEmail()));
			mailDto.setDisplayEmailSignature(true);
			try {
				sendMailService.sendEmail(mailDto);
			} catch (Exception e) {
				response.setMessage(InsuranceCompanyConstants.CERTIFICATE_REVOKE_SUCCESS
						+" "+InsuranceCompanyConstants.MAIL_QUOTA_EXCEED);
				return response;
			}

		}
		return response;
	}

	public Response<List<RevokedReasonsDto>> revokedReasons() {
		Response<List<RevokedReasonsDto>> response = new Response();
		List<RevokedReasonsDto> revokedReasonsList = new ArrayList();
		List<RevokeReason> revokeReasonsData = revokeReasonRepo.findAll();
		RevokedReasonsDto revokedReasonsDto = null;
		for (RevokeReason reason : revokeReasonsData) {
			revokedReasonsDto = new RevokedReasonsDto();
			revokedReasonsDto.setReasonId(reason.getReasonId());
			revokedReasonsDto.setReason(reason.getReason());
			revokedReasonsList.add(revokedReasonsDto);
		}
		response.setData(revokedReasonsList);
		response.setMessage(InsuranceCompanyConstants.CERTIFICATE_REVOKE_REASONS);
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

	public String getQrBase(String certtificateNo) {
		String qrBase64 = null;
		try {
			qrBase64 = QRCodeGenerator.getQRCodeImage(certtificateNo, 300, 300);
		} catch (WriterException e) {
			System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
		}
		return qrBase64;

	}

	public Response<CertificateDTO> searchIssuedCertificates(String value, Integer pageSize, Integer currentPage,
			Integer companyId, String issuedBy, Integer branchId) {
		Response<CertificateDTO> response = new Response<>();

		InsuranceCompany company = companyRepo.findByCompanyId(companyId);

		if (ObjectUtils.isEmpty(company)) {
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
		}
		if(!certificateRepo.existsByInsuranceCompany(company)) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
			return response;
		}
		CompanyBranch companyBranch = companyBranchRepo.findByCompanyBranchId(branchId);
		CertificateDTO dashboardDTO = new CertificateDTO();
		List<IssuedCertificatesDetails> issuedCertificatesDetailsList = new ArrayList<>();
		
		CompanyUserType companyUserType = companyUserTypeRepo.findByUserTypeId(Integer.valueOf(issuedBy));
		List<Integer> userList = userRepo.findByUserIdAndCompany(company.getCompanyId(), companyUserType.getUserTypeId());
		if(userList == null || userList.size()<=0) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
			return response;
		}
		
		int totalIssuedCerts = 0;
		Pageable pagable = PageRequest.of(currentPage, pageSize);
		Page<CertificateDetails> issuedCertificates = null;
		Integer status = getStatusValue(value);
		if (ObjectUtils.isEmpty(companyBranch)) {
		 issuedCertificates = certificateRepo.findByLikeSearch(value, value, value, value, status,
				 userList, company, pagable);
		 
		 totalIssuedCerts = certificateRepo.findByLikeSearch1(value, value, value, value, status,
				 userList, company).size();
		}else {
			 issuedCertificates = certificateRepo.findByLikeSearchBranch(value, value, value,value, status,
					 userList, company, companyBranch.getBranch(), pagable);
			 totalIssuedCerts = certificateRepo.findByLikeSearchBranch1(value, value, value, value, status,
					 userList, company, companyBranch.getBranch()).size();
		}

		List<CertificateDetails> certificateDetails = issuedCertificates.getContent();
		if (certificateDetails.isEmpty()) {
			response.setMessage(InsuranceCompanyConstants.NOT_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
			return response;
		} else {

			for (CertificateDetails certificateDetail : certificateDetails) {
				IssuedCertificatesDetails issuedCertificatesDetails = new IssuedCertificatesDetails();
				issuedCertificatesDetails.setCertificateSerialNumber(certificateDetail.getCertificateSerialNumber());
				issuedCertificatesDetails.setInsured(certificateDetail.getInsured());
				issuedCertificatesDetails.setPolicyNumber(certificateDetail.getPolicyNumber());
				if (certificateDetail.getStatus() == 0) {
					issuedCertificatesDetails.setStatus(CertificateStatus.revoked.toString());
				} else {
					issuedCertificatesDetails.setStatus(CertificateStatus.issued.toString());
				}
				issuedCertificatesDetails.setExpiryDate(certificateDetail.getExpiryDate());
				User user = userRepo.findByUserId(certificateDetail.getUploadedBy());
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
				issuedCertificatesDetails.setCommencingDate(formatter.format(certificateDetail.getCommencingDate()));

				SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm a");
				issuedCertificatesDetails.setCommencingTime(formatterTime.format(certificateDetail.getCommencingDate()));

				issuedCertificatesDetails.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
				issuedCertificatesDetails.setRegistrationNo(certificateDetail.getRegistartionNumber());
				issuedCertificatesDetails.setChassisNo(certificateDetail.getChassisNumber());
				issuedCertificatesDetails.setMarkType(certificateDetail.getVechicleType());

				issuedCertificatesDetails.setQrCode(
						"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetail.getQrCode()));
				
				if (!StringUtils.isEmpty(user.getSignature())) {
					issuedCertificatesDetails.setSignature(
							"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getSignature()));
				}
				
				issuedCertificatesDetails.setUsage(certificateDetail.getUsage());
				issuedCertificatesDetails.setLicensed(certificateDetail.getLicensed());
				issuedCertificatesDetailsList.add(issuedCertificatesDetails);
			}

			dashboardDTO.setIssuedCertificatesDetails(issuedCertificatesDetailsList);
			dashboardDTO.setTotalCertificates(totalIssuedCerts);

			response.setData(dashboardDTO);
			response.setStatus(HttpStatus.OK.value());
		}
		return response;
	}
	private Integer getStatusValue(String value) {
		if(org.apache.commons.lang3.StringUtils.isBlank(value))
			return null;
		if("issued".contains(value.toLowerCase()))
			return 1;
		else if("revoked".contains(value.toLowerCase()))
			return 0;
		return null;
	}

	public Response<CertificateDetailsDTO> getCertificateDetailsByVehicleNo(String vehicleNo) {
		Response<CertificateDetailsDTO> response = new Response<>();
		CertificateDetails certificateDetails = certificateRepo.findByRegistartionNumber(vehicleNo);
		if (certificateDetails == null) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.VEHICLE_NO_AVAILABLE);
			return response;
		}
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		CertificateDetailsDTO certificateDetailsDTO = modelMapper.map(certificateDetails, CertificateDetailsDTO.class);
		certificateDetailsDTO.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
		response.setData(certificateDetailsDTO);
		response.setStatus(200);
		return response;

	}

	public static Date getStartDate(String filter) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startDate = null;

		switch (filter) {
		case "ThisWeek":
			calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
			break;
		case "ThisMonth":
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			break;
		case "ThreeMonths":
			calendar.add(Calendar.MONTH, -3);
			break;
		case "SixMonths":
			calendar.add(Calendar.MONTH, -6);
			break;
		case "ThisYear":
			calendar.add(Calendar.MONTH, -12);
			break;

		}

		startDate = calendar.getTime();

		return startDate;

	}

	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public Response<CertificateDTO> searchIssuedCertificatesReports(Integer pageSize, Integer currentPage,
			Integer companyId, Integer status, String issuedBy, Integer uplodedBy, String startDate, String endDate, List<Integer> branchId, String transactionDate)
			throws ParseException {

		Response<CertificateDTO> response = new Response<>();
		InsuranceCompany company = companyRepo.findByCompanyId(companyId);
		if (ObjectUtils.isEmpty(company)) {
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
		}
		if(!certificateRepo.existsByInsuranceCompany(company)) {
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
			return response;
		}
		List<BranchMaster> branchList =  new ArrayList<>();
		if(branchId != null) {
		List<CompanyBranch> companyBranch = companyBranchRepo.findByCompanyBranchIdIn(branchId);
	    for(CompanyBranch branch : companyBranch) {
	    	branchList.add(branch.getBranch());
	    }
	    
		}
			CertificateDTO dashboardDTO = new CertificateDTO();
			DateTimeFormatter formatterIssuedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		List<IssuedCertificatesDetails> issuedCertificatesDetailsList = new ArrayList<>();
		Pageable pagable = PageRequest.of(currentPage, pageSize);
        StringBuffer queryString = new StringBuffer("Select certificate from CertificateDetails certificate ");
        queryString.append(" where certificate.insuranceCompany=:company");
       
        if(issuedBy != null) {
        	queryString.append(" AND certificate.issuedBy Like :issuedBy") ;
        }
        if(status != null) {
        	queryString.append(" AND certificate.status=:status");
        }
        if(uplodedBy != null) {
        	queryString.append(" AND certificate.uploadedBy=:uplodedBy" );
        }
        if(startDate != null && endDate != null) {
        	queryString.append(" AND certificate.commencingDate BETWEEN :startDate AND :endDate " );
        	queryString.append(" AND certificate.expiryDate BETWEEN :startDate AND :endDate " );
        }
        
        if(branchId != null) {
        	 
        	queryString.append(" AND certificate.branch IN (:branch) ");
        }
        
        if(transactionDate != null) {
        	queryString.append(" AND certificate.createdDate BETWEEN :fromIssedDate AND :toIssedDate " );
        }
        Query query = em.createQuery(queryString.toString());
        query.setParameter( "company", company );
        if(issuedBy != null) {
        	 query.setParameter( "issuedBy", "%" + issuedBy + "%" );
        }
        if(status != null) {
       	 query.setParameter( "status", status );
       }
        if(uplodedBy != null) {
        	 query.setParameter( "uplodedBy", uplodedBy );
        }
        if(startDate  != null && endDate != null) {
          	 query.setParameter( "startDate", DateFormatUtils.formatDateTime(startDate) );
        	 query.setParameter( "endDate", DateFormatUtils.formatDateTime(endDate) );
          }
        
        if(branchId != null) {
       	 query.setParameter( "branch", branchList );
       }
        if(transactionDate != null) {
        	
       	 query.setParameter( "fromIssedDate", LocalDateTime.parse(transactionDate+" 00:00:00", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
       	 query.setParameter( "toIssedDate", LocalDateTime.parse(transactionDate+" 23:59:59", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
       }
        
        int totalCount = query.getResultList().size();
        
        query.setFirstResult(currentPage*pageSize).setMaxResults(pageSize);
        List<CertificateDetails> certificateDetails =   query.getResultList();
		if (certificateDetails.isEmpty()) {
			response.setMessage(InsuranceCompanyConstants.NOT_AVAILABLE);
			response.setStatus(HttpStatus.OK.value());
			return response;
		} else {

			for (CertificateDetails certificateDetail : certificateDetails) {
				IssuedCertificatesDetails issuedCertificatesDetails = new IssuedCertificatesDetails();
				issuedCertificatesDetails.setCertificateSerialNumber(certificateDetail.getCertificateSerialNumber());
				issuedCertificatesDetails.setInsured(certificateDetail.getInsured());
				issuedCertificatesDetails.setPolicyNumber(certificateDetail.getPolicyNumber());
				if (certificateDetail.getStatus() == 0) {
					issuedCertificatesDetails.setStatus(CertificateStatus.revoked.toString());
				} else {
					issuedCertificatesDetails.setStatus(CertificateStatus.issued.toString());
				}
				issuedCertificatesDetails.setExpiryDate(certificateDetail.getExpiryDate());
				User user = userRepo.findByUserId(certificateDetail.getUploadedBy());

				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
				issuedCertificatesDetails.setCommencingDate(formatter.format(certificateDetail.getCommencingDate()));

				SimpleDateFormat formatterTime = new SimpleDateFormat("hh:mm a");
				issuedCertificatesDetails.setCommencingTime(formatterTime.format(certificateDetail.getCommencingDate()));

				issuedCertificatesDetails.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
				issuedCertificatesDetails.setRegistrationNo(certificateDetail.getRegistartionNumber());
				issuedCertificatesDetails.setChassisNo(certificateDetail.getChassisNumber());
				issuedCertificatesDetails.setMarkType(certificateDetail.getVechicleType());

				issuedCertificatesDetails.setQrCode(
						"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetail.getQrCode()));
				issuedCertificatesDetails.setUsage(certificateDetail.getUsage());
				issuedCertificatesDetails.setLicensed(certificateDetail.getLicensed());
				if(certificateDetail.getBranch() != null) {
				issuedCertificatesDetails.setBranch(certificateDetail.getBranch().getBranchName());
				}
				
				issuedCertificatesDetails.setTransactionDate(formatter.format(Date
					      .from(certificateDetail.getCreatedDate().atZone(ZoneId.systemDefault())
					    	      .toInstant())));
				issuedCertificatesDetailsList.add(issuedCertificatesDetails);
			}

			dashboardDTO.setIssuedCertificatesDetails(issuedCertificatesDetailsList);
			dashboardDTO.setTotalCertificates(totalCount);
			
			response.setData(dashboardDTO);
			response.setStatus(HttpStatus.OK.value());
		}
		return response;
	}
	
	 private String getCompanyUserTypeByUserId(int userTypeId) {
   	  return  companyUserTypeRepo.findByUserTypeId(userTypeId).getUserType();
     }

}
