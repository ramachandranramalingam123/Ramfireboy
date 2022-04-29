package com.beyontec.mdcp.service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.beyontec.mdcp.dto.AutoApprovalDto;
import com.beyontec.mdcp.dto.CertificateDTO;
import com.beyontec.mdcp.dto.CompanyAddDto;
import com.beyontec.mdcp.dto.CompanyCertificateStatusDto;
import com.beyontec.mdcp.dto.InsuranceCompanies;
import com.beyontec.mdcp.dto.InsuranceCompanyListDTO;
import com.beyontec.mdcp.dto.IssuedCertificatesDetails;
import com.beyontec.mdcp.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.model.BranchMaster;
import com.beyontec.mdcp.model.CertificateDetails;
import com.beyontec.mdcp.model.CompanyBranch;
import com.beyontec.mdcp.model.CompanyUserBranch;
import com.beyontec.mdcp.model.CompanyUserType;
import com.beyontec.mdcp.model.InsuranceCompany;
import com.beyontec.mdcp.model.User;
import com.beyontec.mdcp.repo.BranchMasterRepo;
import com.beyontec.mdcp.repo.CertificateAllocationRepo;
import com.beyontec.mdcp.repo.CertificateRepo;
import com.beyontec.mdcp.repo.CertificateSerialRepo;
import com.beyontec.mdcp.repo.CompanyBranchRepo;
import com.beyontec.mdcp.repo.CompanyRepo;
import com.beyontec.mdcp.repo.CompanyUserBranchRepo;
import com.beyontec.mdcp.repo.CompanyUserTypeRepo;
import com.beyontec.mdcp.repo.InternalCertificateRepo;
import com.beyontec.mdcp.repo.UserRepo;
import com.beyontec.mdcp.response.Response;
import com.beyontec.mdcp.util.CertificateStatus;
import com.beyontec.mdcp.util.DateFormatUtils;
import com.beyontec.mdcp.util.MotorAuthorityConstants;
import com.beyontec.mdcp.util.QRCodeGenerator;
import com.google.zxing.WriterException;

@Service
public class CompanyService {

	@Autowired
	private CompanyRepo companyRepo;

	@Autowired
	private CertificateRepo certificateRepo;

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
	private InternalCertificateRepo internalCertificateRepo;
	
	@Autowired
	private CompanyUserTypeRepo companyUserTypeRepo;

	@Autowired
	private CompanyUserBranchRepo companyUserBranchRepo;
	
	public Response<InsuranceCompanies> getInsuranceCompanies(int pageSize, int currentPage, String companyName) {

		Pageable pagable = PageRequest.of(currentPage, pageSize);
		Response<InsuranceCompanies> response = new Response<>();
		InsuranceCompanies companies = new InsuranceCompanies();

		Page<InsuranceCompany> insuranceCompaniesList = null;
		if (StringUtils.isEmpty(companyName)) {
			insuranceCompaniesList = companyRepo.findAllByStatusOrderByCreatedDateDesc("A", pagable);
		} else {
			insuranceCompaniesList = companyRepo.findByCompanyNameContaining(companyName, pagable);
		}
		companies.setTotalCompanies(insuranceCompaniesList.getTotalElements());
		return mapCompanyData(response, companies, insuranceCompaniesList.getContent());
	}

	public Response<CertificateDTO> getCertificateDetails(Integer companyId, int pageSize, int currentPage,
			String filter, String firstDate, String endDate) throws ParseException {

		Response<CertificateDTO> response = new Response<>();
		CertificateDTO certificateDTO = new CertificateDTO();
		List<IssuedCertificatesDetails> issuedCertificatesDetailsList = new ArrayList<>();

		Pageable pagable = PageRequest.of(currentPage, pageSize);

		InsuranceCompany insuranceCompany = companyRepo.findByCompanyId(companyId);
		if (insuranceCompany == null)
			throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_NOT_FOUND);

		List<CertificateDetails> certificateDetails = null;
		if (!StringUtils.isEmpty(filter)) {
			Date startDate = getStartDate(filter);

			certificateDetails = certificateRepo.findAllByInsuranceCompanyAndCreatedDateBetweenOrderByCreatedDateDesc(
					insuranceCompany, convertToLocalDateTimeViaInstant(startDate), LocalDateTime.now(), pagable);
		} else {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			certificateDetails = certificateRepo.findAllByInsuranceCompanyAndCreatedDateBetweenOrderByCreatedDateDesc(
					insuranceCompany, LocalDateTime.parse(firstDate, formatter),
					LocalDateTime.parse(endDate, formatter), pagable);
		}

		if (certificateDetails == null || certificateDetails.isEmpty() || certificateDetails.size() <= 0) {

			Long allocatedCertificatesSum = certificateAllocationRepo
					.getSumOfAllocatedCertificateByAuthority(insuranceCompany);
			Long totalCertificateByAuthoritySum = certificateAllocationRepo
					.getSumOfAllocatedCertificateByAuthority(insuranceCompany);
			Long pendingCertificatesSum = certificateAllocationRepo.getSumOfAllocatedCertificate(insuranceCompany);
			Long totalCertificates = certificateAllocationRepo.getSumOfAllocatedCertificate(insuranceCompany);
			if (allocatedCertificatesSum != null) {
				certificateDTO.setAllocatedCertificates(allocatedCertificatesSum);
			} else {
				certificateDTO.setAllocatedCertificates((long) 0);
			}
			if (totalCertificateByAuthoritySum != null) {
				certificateDTO.setTotalCertificateByAuthority(totalCertificateByAuthoritySum);
			} else {
				certificateDTO.setTotalCertificateByAuthority((long) 0);
			}
			certificateDTO.setIssuedCertificates((long) 0);
			if (pendingCertificatesSum != null) {
				certificateDTO.setPendingCertificates(
						pendingCertificatesSum - certificateRepo.countByCompany(insuranceCompany));
			} else {
				certificateDTO.setPendingCertificates((long) 0);
			}
			if (totalCertificates != null) {
				certificateDTO.setTotalCertificates(totalCertificates);
			} else {
				certificateDTO.setTotalCertificates((long) 0);
			}
			response.setData(certificateDTO);
			response.setStatus(HttpStatus.OK.value());
		} else {

			for (CertificateDetails certificateDetail : certificateDetails) {
				IssuedCertificatesDetails issuedCertificatesDetails = new IssuedCertificatesDetails();
				issuedCertificatesDetails.setCertificateSerialNumber(certificateDetail.getCertificateSerialNumber());
				issuedCertificatesDetails.setInsured(certificateDetail.getInsured());
				issuedCertificatesDetails.setPolicyNumber(certificateDetail.getPolicyNumber());
				issuedCertificatesDetails.setCommencingDate(DateFormatUtils
						.formatDateSql(DateFormatUtils.outputSDFSQL.format(certificateDetail.getCommencingDate())));
				issuedCertificatesDetails.setExpiryDate(DateFormatUtils
						.formatDateSql(DateFormatUtils.outputSDFSQL.format(certificateDetail.getExpiryDate())));
				User user = userRepo.findByUserId(certificateDetail.getUploadedBy());
				// issuedCertificatesDetails.setExpiryDate(certificateDetail.getExpiryDate());
				issuedCertificatesDetails.setRegistrationNo(certificateDetail.getRegistartionNumber());
				CompanyUserType companyUserType =companyUserTypeRepo.findByUserTypeId(user.getUserTypeId());
			//	CompanyUserType companyUserType = user.getCompanyUserType();
				System.out.println(companyUserType.getUserType() + ".........");
				issuedCertificatesDetails.setIssuedBy(companyUserType.getUserType());
				issuedCertificatesDetails.setChassisNo(certificateDetail.getChassisNumber());
				issuedCertificatesDetails.setMarkType(certificateDetail.getVechicleType());
				issuedCertificatesDetails.setQrCode(getQrBase(certificateDetail.getCertificateSerialNumber()));
				issuedCertificatesDetails.setQrCode(
						"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetail.getQrCode()));
				issuedCertificatesDetails.setLicensed(certificateDetail.getLicensed());
				issuedCertificatesDetails.setUsage(certificateDetail.getUsage());
				if (!org.springframework.util.StringUtils.isEmpty(user.getSignature())) {
					issuedCertificatesDetails.setSignature(
							"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getSignature()));
				}
				if (certificateDetail.getStatus() == 0) {
					issuedCertificatesDetails.setStatus(CertificateStatus.revoked.name());
				} else {
					issuedCertificatesDetails.setStatus(CertificateStatus.issued.name());
				}
				issuedCertificatesDetailsList.add(issuedCertificatesDetails);
			}
			certificateDTO.setIssuedCertificatesDetails(issuedCertificatesDetailsList);
			Long allocatedCertificates = certificateAllocationRepo
					.getSumOfAllocatedCertificateByAuthority(insuranceCompany);
			if (allocatedCertificates != null) {
				certificateDTO.setAllocatedCertificates(allocatedCertificates);
			} else {
				certificateDTO.setAllocatedCertificates((long) 0);
			}
			certificateDTO.setIssuedCertificates(certificateRepo.countByCompany(insuranceCompany));
			Long totalCertificateByAuthority = certificateAllocationRepo
					.getSumOfAllocatedCertificateByAuthority(insuranceCompany);
			if (totalCertificateByAuthority != null) {
				certificateDTO.setTotalCertificateByAuthority(totalCertificateByAuthority);
			} else {
				certificateDTO.setTotalCertificateByAuthority((long) 0);
			}
			Long pendingCertificates = certificateAllocationRepo.getSumOfAllocatedCertificate(insuranceCompany);
			if (pendingCertificates != null) {
				certificateDTO
						.setPendingCertificates(pendingCertificates - certificateRepo.countByCompany(insuranceCompany));
			} else {
				certificateDTO.setPendingCertificates((long) 0);
			}
			Long tsetTotalCertificates = certificateAllocationRepo.getSumOfAllocatedCertificate(insuranceCompany);
			if (tsetTotalCertificates != null) {
				certificateDTO.setTotalCertificates(tsetTotalCertificates);
			} else {
				certificateDTO.setTotalCertificates((long) 0);
			}
			response.setData(certificateDTO);
			response.setStatus(HttpStatus.OK.value());
		}
		return response;

	}

	private Response<InsuranceCompanies> mapCompanyData(Response<InsuranceCompanies> response,
			InsuranceCompanies companies, List<InsuranceCompany> insuranceCompaniesList) {
		List<InsuranceCompanyListDTO> companyDtoList = new ArrayList<>();
		for (InsuranceCompany company : insuranceCompaniesList) {
			Long totalCertificateByAuthoritySum = certificateAllocationRepo
					.getSumOfAllocatedCertificateByAuthority(company);
			Long totalCertificatesSum = certificateAllocationRepo.getSumOfAllocatedCertificate(company);
			InsuranceCompanyListDTO companyDto = new InsuranceCompanyListDTO();
			companyDto.setCompanyId(company.getCompanyId());
			companyDto.setCompanyName(company.getCompanyName());
			if (totalCertificateByAuthoritySum != null) {
				companyDto.setTotalCertificateByAuthority(totalCertificateByAuthoritySum);
			} else {
				companyDto.setTotalCertificateByAuthority((long) 0);
			}
			if (totalCertificatesSum != null) {
				companyDto.setTotalCertificates(totalCertificatesSum - certificateRepo.countByCompany(company));
			} else {
				companyDto.setTotalCertificates((long) 0);
			}
			List<CompanyBranch> companyBranch = companyBranchRepo.findByCompany(company);
			StringBuffer sb = new StringBuffer();
			if (companyBranch != null && companyBranch.size() > 0) {
				for (CompanyBranch companyBranchData : companyBranch) {
					if (companyBranchData.getBranch().getBranchName() != null) {
						sb.append(companyBranchData.getBranch().getBranchName());
						sb.append(",");
					}
				}
				if (sb != null) {
					sb.deleteCharAt(sb.length() - 1);
					companyDto.setBranchName(sb.toString());
				}
			}
			companyDto.setCode(company.getCompanyCode());
			companyDto.setAddress(company.getAddress1());
			companyDto.setAddress1(company.getAddress1());
			companyDto.setAddress2(company.getAddress2());
			companyDto.setCity(company.getCity());
			companyDto.setState(company.getState());
			companyDto.setCountry(company.getCountry());
			companyDto.setMailId(company.getMailId());
			companyDto.setPostBoxCode(company.getZipCode());
			if (!ObjectUtils.isEmpty(company.getCompanyLogo())) {
				companyDto.setCompanyLogo(
						"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(company.getCompanyLogo()));
			}
			companyDto.setContactNumber(company.getContactNo());
			companyDto.setAutoApprovalLimit(company.getAutoApprovalLimit());
			companyDto.setAutoApprovalTime(company.getAutoApprovalTime());

			companyDtoList.add(companyDto);
		}

		companies.setInsuranceCompanies(companyDtoList);
		response.setData(companies);
		response.setStatus(HttpStatus.OK.value());
		return response;
	}

	public Response<Map<String, Long>> countAllIsssuedCertificates() {

		Response<Map<String, Long>> response = new Response<>();
		Map<String, Long> data = new HashMap<>();
		Long authorityToBeAllocated = certificateAllocationRepo.getSumOfAuthorityToBeAllocateCertificate();
		if (authorityToBeAllocated != null) {
			data.put("count", authorityToBeAllocated);
		} else {
			data.put("count", (long) 0);
		}
		response.setData(data);
		response.setStatus(HttpStatus.OK.value());
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
		if (companyAddDto.getBranchName() != null) {
			BranchMaster branchMaster = null;
			CompanyBranch companyBranch = null;
			if (companyAddDto.getBranchName().contains(",")) {
				String[] branches = companyAddDto.getBranchName().split(",");
				for (int i = 0; i < branches.length; i++) {
					BranchMaster branchMasterData = branchMasterRepo.findByBranchNameAndCompany(branches[i],newCompany);
					if (branchMasterData == null) {
						branchMaster = new BranchMaster();
						branchMaster.setBranchName(branches[i]);
						branchMaster.setCompany(newCompany);
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
			} else {
				BranchMaster branchMasterData = branchMasterRepo.findByBranchNameAndCompany(companyAddDto.getBranchName(),newCompany);
				if (branchMasterData == null) {
					branchMaster = new BranchMaster();
					branchMaster.setBranchName(companyAddDto.getBranchName());
					branchMaster.setCompany(newCompany);
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
	}

	public Response<String> editAutoApprovals(List<AutoApprovalDto> autoApprovalDtos) {
		Response<String> response = new Response<String>();

		for (AutoApprovalDto autoApprovalDto : autoApprovalDtos) {
			InsuranceCompany company = companyRepo.findByCompanyId(autoApprovalDto.getCompanyId());

			if (ObjectUtils.isEmpty(company)) {

				throw new BadDataExceptionHandler(MotorAuthorityConstants.COMPANY_NOT_FOUND);
			}

			company.setAutoApprovalLimit(autoApprovalDto.getAppriovalLimit());
			company.setAutoApprovalTime(autoApprovalDto.getApprovalTime());

			companyRepo.save(company);
		}
		response.setStatus(HttpStatus.OK.value());
		response.setMessage(MotorAuthorityConstants.AUTO_APPROVALS_UPDATED);
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

	public Response<String> addCompanyDetails(CompanyAddDto companyAddDto) {

		Response<String> response = new Response<String>();
System.out.println("CID>>>>>>>>>>>>>>>>>>>>>>>>>>>"+companyAddDto.getCompanyId());

		if (companyAddDto.getCompanyId() == null) {
			System.out.println("CID>>>INSID>>>>>>>>>>>>>>>>>>>>>>>>"+companyAddDto.getCompanyId());
			InsuranceCompany company = companyRepo.findByCompanyCode(companyAddDto.getCompanyCode());

			if (!ObjectUtils.isEmpty(company)) {

				response.setStatus(200);
				response.setMessage(MotorAuthorityConstants.COMPANY_CODE_INVALID);
				return response;
			}
			addNewCompany(companyAddDto);
			response.setStatus(HttpStatus.OK.value());
			response.setData(companyAddDto.getCompanyName());
			response.setMessage(MotorAuthorityConstants.COMPANY_SUCCESSFULLY_CREATED);
		} else {
			System.out.println("CID>>>>>>EDIT>>>>>>>>>>>>>>>>>>>>"+companyAddDto.getCompanyId());
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
			if (companyAddDto.getBranchName() != null) {
				BranchMaster branchMaster = null;
				CompanyBranch companyBranch = null;

				List<BranchMaster> branchMasterList = branchMasterRepo.findByCompany(companyData);
				if (companyAddDto.getBranchName().contains(",")) {
					String[] branches = companyAddDto.getBranchName().split(",");
					List<BranchMaster> availaleBranchMasList = new ArrayList<>();
					for (int i = 0; i < branches.length; i++) {
						for (BranchMaster bm : branchMasterList) {
							if (branches[i].equalsIgnoreCase(bm.getBranchName())) {
								availaleBranchMasList.add(bm);
								break;
							}
						}
						BranchMaster branchMasterData = branchMasterRepo.findByBranchNameAndCompany(branches[i],companyData);
						if (branchMasterData == null) {
							branchMaster = new BranchMaster();
							branchMaster.setBranchName(branches[i]);
							branchMaster.setCompany(companyData);
							branchMaster.setStatus(0);
							branchMaster.setCreatedDate(LocalDateTime.now());
							branchMaster.setCreatedBy(companyAddDto.getUserId());
							branchMaster = branchMasterRepo.save(branchMaster);
							companyBranch = new CompanyBranch();
							companyBranch.setBranch(branchMaster);
							companyBranch.setCompany(companyData);
							companyBranch.setCreatedBy(companyAddDto.getUserId());
							companyBranch.setCreatedDate(LocalDateTime.now());
							companyBranchRepo.save(companyBranch);
							if (companyBranch != null) {
								CompanyUserBranch companyUserBranch = null;
								User user = userRepo.findByUserId(companyAddDto.getUserId());
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
					boolean isBranchAvailable = false;
					for (BranchMaster bm : branchMasterList) {
						isBranchAvailable = false;
						for (BranchMaster availableBranchMaster : availaleBranchMasList) {
							if (bm.getBranchName().equalsIgnoreCase(availableBranchMaster.getBranchName())) {
								isBranchAvailable = true;
								break;
							}
						}
						if (!isBranchAvailable)	{
							List<CompanyUserBranch> companyUserBranch = companyUserBranchRepo.findByBranch(bm);
							CompanyBranch compBranch = companyBranchRepo.findByBranch(bm);
							if (companyUserBranch != null) {
								companyUserBranchRepo.deleteAll(companyUserBranch);
							}
							
							if (compBranch != null) {
								companyBranchRepo.delete(compBranch);
							}

							if (bm != null) {
								branchMasterRepo.delete(bm);
							}
						}
					}
					
				} else {
					BranchMaster branchMasterData = branchMasterRepo.findByBranchNameAndCompany(companyAddDto.getBranchName(),companyData);
					if (branchMasterData == null) {
						branchMaster = new BranchMaster();
						branchMaster.setBranchName(companyAddDto.getBranchName());
						branchMaster.setCompany(companyData);
						branchMaster.setStatus(0);
						branchMaster.setCreatedDate(LocalDateTime.now());
						branchMaster.setCreatedBy(companyAddDto.getUserId());
						branchMaster = branchMasterRepo.save(branchMaster);
						companyBranch = new CompanyBranch();
						companyBranch.setBranch(branchMaster);
						companyBranch.setCompany(companyData);
						companyBranch.setCreatedBy(companyAddDto.getUserId());
						companyBranch.setCreatedDate(LocalDateTime.now());
						companyBranchRepo.save(companyBranch);
						if (companyBranch != null) {
							CompanyUserBranch companyUserBranch = null;
							User user = userRepo.findByUserId(companyAddDto.getUserId());
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
					for (BranchMaster bm : branchMasterList) {
						if (!bm.getBranchName().equalsIgnoreCase(companyAddDto.getBranchName())) {
							List<CompanyUserBranch> companyUserBranch = companyUserBranchRepo.findByBranch(bm);
							CompanyBranch compBranch = companyBranchRepo.findByBranch(bm);
							if (companyUserBranch != null) {
								companyUserBranchRepo.deleteAll(companyUserBranch);
							}
							
							if (compBranch != null) {
								companyBranchRepo.delete(compBranch);
							}

							if (bm != null) {
								branchMasterRepo.delete(bm);
							}
						}
					}
				}
				
			}

			response.setStatus(HttpStatus.OK.value());
			response.setData(companyAddDto.getCompanyName());
			response.setMessage(MotorAuthorityConstants.COMPANY_SUCCESSFULLY_UPDATED);
		}

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

	public Response<String> deleteCompany(int companyId, int loginId) {

		Response<String> response = new Response<>();
		InsuranceCompany company = companyRepo.findByCompanyId(companyId);

		if (ObjectUtils.isEmpty(company)) {
			response.setStatus(200);
			response.setMessage(MotorAuthorityConstants.COMPANY_NOT_FOUND);
			return response;
		}

		company.setStatus("I");
		company.setUpdatedBy(loginId);
		company.setUpdatedDate(LocalDateTime.now());
		companyRepo.save(company);
		response.setData(company.getCompanyId().toString());
		response.setMessage(MotorAuthorityConstants.COMPANY_REMOVE);
		response.setStatus(200);
		return response;
	}

	public Response<CompanyCertificateStatusDto> companyCertificateStatusDetails() {
		Response<CompanyCertificateStatusDto> response = new Response();
		
		  CompanyCertificateStatusDto companyCertificateStatusDto = new CompanyCertificateStatusDto();
		  
		    List<InsuranceCompany> activeCompanies = companyRepo.findAllByStatus("A");
			Long allocatedCerts = certificateAllocationRepo.getTotalllocatedCertificatesByStatus(activeCompanies);
			Long issudeCertsCount = certificateRepo.countByIssuedCerts();
			if(allocatedCerts == null || allocatedCerts == 0) {
				companyCertificateStatusDto.setAllocatedCertificates((long) 0);
				companyCertificateStatusDto.setIssuedCertificates((long) 0);
				companyCertificateStatusDto.setPendingCertificates((long) 0);
			}else {
			companyCertificateStatusDto.setAllocatedCertificates(allocatedCerts);
			if(issudeCertsCount != null) {
				companyCertificateStatusDto.setIssuedCertificates(issudeCertsCount);
				companyCertificateStatusDto.setPendingCertificates(allocatedCerts-issudeCertsCount);
			}else {
				companyCertificateStatusDto.setIssuedCertificates((long) 0);
				companyCertificateStatusDto.setPendingCertificates(allocatedCerts);

			}
			}
			response.setData(companyCertificateStatusDto);
			response.setStatus(200);
			response.setMessage("Certificates Details");
		return response;
	}

}
