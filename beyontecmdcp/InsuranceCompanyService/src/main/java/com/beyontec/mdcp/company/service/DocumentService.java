package com.beyontec.mdcp.company.service;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.beyontec.mdcp.company.dto.CertificateIssueDto;
import com.beyontec.mdcp.company.dto.RevokeCertificateDTO;
import com.beyontec.mdcp.company.exception.BadDataExceptionHandler;
import com.beyontec.mdcp.company.model.BranchMaster;
import com.beyontec.mdcp.company.model.CertificateDetails;
import com.beyontec.mdcp.company.model.CertificateRevoke;
import com.beyontec.mdcp.company.model.CertificateSerialNum;
import com.beyontec.mdcp.company.model.CompanyBranch;
import com.beyontec.mdcp.company.model.CompanyUserType;
import com.beyontec.mdcp.company.model.InsuranceCompany;
import com.beyontec.mdcp.company.model.RevokeReason;
import com.beyontec.mdcp.company.model.User;
import com.beyontec.mdcp.company.repo.CertificateRepo;
import com.beyontec.mdcp.company.repo.CertificateRevokeRepo;
import com.beyontec.mdcp.company.repo.CertificateSerialRepo;
import com.beyontec.mdcp.company.repo.CompanyBranchRepo;
import com.beyontec.mdcp.company.repo.CompanyRepo;
import com.beyontec.mdcp.company.repo.CompanyUserTypeRepo;
import com.beyontec.mdcp.company.repo.RevokeReasonRepo;
import com.beyontec.mdcp.company.repo.UserRepo;
import com.beyontec.mdcp.company.response.Response;
import com.beyontec.mdcp.company.util.CertificateIssuesUtil;
import com.beyontec.mdcp.company.util.CertificateStatus;
import com.beyontec.mdcp.company.util.DateFormatUtils;
import com.beyontec.mdcp.company.util.InsuranceCompanyConstants;
import com.beyontec.mdcp.company.util.QRCodeGenerator;
import com.google.zxing.WriterException;

@Service
public class DocumentService {
	
	@Autowired
	private CertificateRepo certificateRepo;
	
	@Autowired
	private CertificateSerialRepo certificateSerialRepo;
	
	@Autowired
	private CompanyRepo companyRepo;

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
	private CertificateIssuesUtil certificateIssuesUtil;
    
    @Autowired
	private UserRepo userRepo;
    
    @Autowired
	private CompanyUserTypeRepo companyUserTypeRepo;
    
    @Autowired
	private CompanyBranchRepo companyBranchRepo;
    
    @Autowired
    private ImageGenerateService imageGenerateService;
    
	@Autowired
	private CertificateRevokeRepo certificateRevokeRepo;
	
	@Autowired
	private RevokeReasonRepo reasonRepo;
    
    @PersistenceContext
	 private EntityManager em;

	public  ByteArrayInputStream certificateIssueToExcel(Integer companyId, Integer userTypeId) throws IOException {
		String[] COLUMNs =  InsuranceCompanyConstants.ONLINE_XLSX_DOWNLOAD_CERTIFICATE_HEADER;
		    try(
		        Workbook workbook = new XSSFWorkbook();
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ){
		      CreationHelper createHelper = workbook.getCreationHelper();
		      Sheet sheet = workbook.createSheet("certificate");
		     
		      Font headerFont = workbook.createFont();
		      headerFont.setBold(true);
		      headerFont.setColor(IndexedColors.BLUE.getIndex());
		   
		      CellStyle headerCellStyle = workbook.createCellStyle();
		      headerCellStyle.setFont(headerFont);
		      Row headerRow = sheet.createRow(0);
		      for (int col = 0; col < COLUMNs.length; col++) {
		        Cell cell = headerRow.createCell(col);
		        cell.setCellValue(COLUMNs[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet.setColumnWidth(col, 25 * 200);
		       
		        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			      cell.setCellStyle(headerCellStyle);    
		       
		      }
		      InsuranceCompany companyData = companyRepo.findByCompanyId(companyId);
		      CompanyUserType companyUserType = companyUserTypeRepo.findByUserTypeId(userTypeId);
		      List<Integer> users = userRepo.findByUserIdAndCompany(companyId,companyUserType.getUserTypeId());
		      List<CertificateDetails> certificateDetails = certificateRepo.findAllByInsuranceCompanyAndUploadedByIn(companyData, users);
		    if(certificateDetails == null || certificateDetails.size()<=0) {
		    	throw new BadDataExceptionHandler(InsuranceCompanyConstants.CERTIFICATE_NOT_AVAILABLE);
		    }
		      Cell cell  = null;
		      for (int i = 0; i <certificateDetails.size(); i++) {
		    	  Row dataRow = sheet.createRow(i+1);
		    	   cell = dataRow.createCell(0); 
		    	   cell.setCellValue(certificateDetails.get(i).getInsured());
		    	   cell = dataRow.createCell(1);
		    	   cell.setCellValue(certificateDetails.get(i).getPolicyNumber());
		    	   cell = dataRow.createCell(2);
		    	   String commencingDateStr =  DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.get(i).getCommencingDate(), true);
		    	   cell.setCellValue(commencingDateStr);
		    	   cell = dataRow.createCell(3);
		    	   String expiryDateStr =  DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.get(i).getExpiryDate(), false);
		    	   cell.setCellValue(expiryDateStr);
		    	   cell = dataRow.createCell(4);
		    	   cell.setCellValue(certificateDetails.get(i).getRegistartionNumber());
		    	   cell = dataRow.createCell(5);
		    	   cell.setCellValue(certificateDetails.get(i).getChassisNumber());
		    	   cell = dataRow.createCell(6);
		    	   cell.setCellValue(certificateDetails.get(i).getVechicleType());
		    	   cell = dataRow.createCell(7);
		    	   cell.setCellValue(certificateDetails.get(i).getLicensed());
		    	   cell = dataRow.createCell(8);
		    	   cell.setCellValue(certificateDetails.get(i).getUsage());
		    	   cell = dataRow.createCell(9);
		    	   cell.setCellValue(certificateDetails.get(i).getPrimaryEmail());
		    	   cell = dataRow.createCell(10);
		    	   cell.setCellValue(certificateDetails.get(i).getIssuedBy());
		    	   
			      }
		      workbook.write(out);
		      return new ByteArrayInputStream(out.toByteArray());
		    }
		  }


	public Response<String> importIssueCertificate(MultipartFile files, Integer companyId, Integer userId, Integer branchId)
			throws IOException, ParseException, EncryptedDocumentException, InvalidFormatException {

//		String filename = files.getOriginalFilename();
//		String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
//	    String excel = "xlsx";
//	    if (!extension.equals(excel))
//	    	throw new BadDataExceptionHandler(InsuranceCompanyConstants.INVALID_IMPORTED_FILE);	
//		
		InsuranceCompany companyData = companyRepo.findByCompanyId(companyId);
		if (companyData == null)
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);

		Response<String> response = new Response<String>();
		Map<Integer, String> headerOrderMap = new HashMap<>();
		Workbook workbook = WorkbookFactory.create(files.getInputStream());

		Sheet worksheet = workbook.getSheetAt(0);
		Row headerrow = worksheet.getRow(0);

		if (ObjectUtils.isEmpty(headerrow) || ObjectUtils.isEmpty(worksheet)) {
			response.setStatus(400);
			response.setMessage(InsuranceCompanyConstants.INVALID_XLSX_FILE);
			return response;
		}
		headerrow.getPhysicalNumberOfCells();
		for (int cell = 0; cell < headerrow.getPhysicalNumberOfCells(); cell++) {
			headerOrderMap.put(cell + 1, headerrow.getCell(cell).getStringCellValue());
		}
		
		if (sheetHeaderValidation(headerOrderMap)) {
			
			String query = "SELECT csn FROM CertificateSerialNum csn " + "where csn.company.companyId=" + "'" + companyId
					+ "' and csn.issuedStatus='N' ORDER BY csn.serialNumOrder ASC";
			
			List<CertificateSerialNum> certSerialNumList = entityManager
					.createQuery(query, CertificateSerialNum.class)
					.setMaxResults(worksheet.getPhysicalNumberOfRows()).getResultList();

			if (certSerialNumList == null || certSerialNumList.isEmpty() || certSerialNumList.size() <= 0) {

				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NOT_AVAILABLE);
				return response;
			}

			if (worksheet.getPhysicalNumberOfRows() < 2) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage(InsuranceCompanyConstants.INVALID_IMPORTED_FILE);
				return response;
			}

			List<CertificateDetails> certificateDetailsList = new ArrayList<>();
			List<CertificateSerialNum> certificateSerialNumList = new ArrayList<>();
			BranchMaster branch = null;
			if (branchId != null) {
				branch = companyBranchRepo.findByCompanyBranchId(branchId).getBranch();
			}
			
			if (headerOrderMap.get(1).equals(InsuranceCompanyConstants.OFFLINE_XLSX_UPLOAD_CERTIFICATE_HEADER[0])) {
				response = populateOfflineCertificateData(worksheet, certificateDetailsList, certSerialNumList,
						companyData, userId, branch);
			} else {
				response = populateOnlineCertificateData(worksheet, certificateDetailsList, certSerialNumList,
						certificateSerialNumList, companyData, userId, branch);
			}
			
			if(response.getStatus() == HttpStatus.BAD_REQUEST.value())
				return response;
			
			certificateRepo.saveAll(certificateDetailsList);
			certificateSerialRepo.saveAll(certificateSerialNumList);
						
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.SUCCESSFULLY_CERTIFICATES_ISSUED);
		} else {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(InsuranceCompanyConstants.UNABLE_TO_CERTIFICATE_ISSUE_BAD_HEADERS);
		}
		return response;
	}
	
	private Response<String> populateOfflineCertificateData(Sheet worksheet,
			List<CertificateDetails> certificateDetailsList,
			List<CertificateSerialNum> certificateSerialNumList, InsuranceCompany companyData, Integer userId, BranchMaster branch) {
		
		CertificateDetails certificateDetails = null;
		CertificateSerialNum certificateSerialNum = null;
		Response<String> response = new Response<String>();
		
		try {
			for (int row = 1; row < worksheet.getPhysicalNumberOfRows(); row++) {
				Row rowData = worksheet.getRow(row);
				
				certificateDetails = new CertificateDetails();
				String certificateSerialNo = castToString(rowData.getCell(0));
				if(StringUtils.isBlank(certificateSerialNo))
					continue;
				
				certificateSerialNum = certificateSerialRepo.findBySerialNum(certificateSerialNo);
				if (certificateSerialNum == null) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_ALREADY_ISSUED);
					return response;
				}
				
				CertificateDetails certificateSerialNumCheck = certificateRepo
						.findByCertificateSerialNumber(certificateSerialNo);

				if (certificateSerialNumCheck != null) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_ALREADY_ISSUED);
					return response;
				}
				certificateDetails.setCertificateSerialNumber(castToString(rowData.getCell(0)));
				
				certificateDetails.setInsured(castToString(rowData.getCell(1)));
				certificateDetails.setPolicyNumber(castToString(rowData.getCell(2)));
				
				try {
					certificateDetails.setCommencingDate(DateFormatUtils.getUpoadFormattedDateTime(rowData.getCell(3)));
					certificateDetails.setExpiryDate(DateFormatUtils.getUpoadFormattedDateTime(rowData.getCell(4)));

				} catch (Exception e) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage("Date format should be dd/mm/yyyy");
					return response;
				}
				
				certificateDetails.setRegistartionNumber(castToString(rowData.getCell(5)));
				certificateDetails.setChassisNumber(castToString(rowData.getCell(6)));
				certificateDetails.setVechicleType(castToString(rowData.getCell(7)));
				if (isValidLicencedData(rowData.getCell(8))) {
					certificateDetails.setLicensed(castToString(rowData.getCell(8)));
				} else {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage("Invalid Licensed To Carry Data");
					return response;
				}
				certificateDetails.setUsage(castToString(rowData.getCell(9)));
				
					String emailDataCell = castToString(rowData.getCell(10));
					if(emailDataCell.contains(",")) {
						String[] emailData = emailDataCell.split(",");
						certificateDetails.setPrimaryEmail(emailData[0]);
						StringBuffer secondaryEmail = new StringBuffer();
						for(int i = 1; i<emailData.length; i++) {
							secondaryEmail.append(emailData[i]);
							secondaryEmail.append(",");
						}
						secondaryEmail.deleteCharAt(secondaryEmail.length()-1);
						certificateDetails.setSecondaryEmail(secondaryEmail.toString());
					}else {
						certificateDetails.setPrimaryEmail(emailDataCell);
					}
			
				certificateDetails.setIssuedBy(castToString(rowData.getCell(13)));
				certificateDetails.setApprovedBy(castToString(rowData.getCell(12)));
				certificateDetails.setStatus(CertificateStatus.issued.getStatus());
				certificateDetails.setInsuranceCompany(companyData);
				
				String qrCodeBase64 = getQrBase(certificateDetails.getCertificateSerialNumber());
				certificateDetails.setQrCode(Base64.getDecoder().decode(qrCodeBase64));
				certificateDetails.setUploadedBy(userId);
				certificateDetails.setCreatedDate(LocalDateTime.now());
				certificateDetails.setIssuedDate(LocalDate.now());
				certificateDetails.setBranch(branch);
				
				certificateDetails.setMailStatus("N");
				certificateDetails.setIsOfflineCertificate(1);
				certificateDetailsList.add(certificateDetails);
				certificateSerialNum.setIssuedStatus("Y");
				certificateSerialNumList.add(certificateSerialNum);
			}
		
		}catch (Exception e) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
		}
		
		response.setStatus(HttpStatus.OK.value());
		return response;
	}


	private Response<String> populateOnlineCertificateData(Sheet worksheet,
			List<CertificateDetails> certificateDetailsList, List<CertificateSerialNum> certificateSerialNum,
			List<CertificateSerialNum> certificateSerialNumList, InsuranceCompany companyData, Integer userId, BranchMaster branch) {
		
		CertificateDetails certificateDetails = null;
		CertificateSerialNum CertificateSerialNum = null;
		Response<String> response = new Response<String>();
		
		try {
			for (int row = 1; row < worksheet.getPhysicalNumberOfRows(); row++) {
				Row rowData = worksheet.getRow(row);
				/*
				 * if(rowDataNullCheck(rowData)) throw new
				 * BadDataExceptionHandler(InsuranceCompanyConstants.
				 * UNABLE_TO_CERTIFICATE_ISSUE_VALUE_NULL);
				 */
				certificateDetails = new CertificateDetails();

				CertificateSerialNum = certificateSerialNum.get(row - 1);

				CertificateDetails certificateSerialNumCheck = certificateRepo
						.findByCertificateSerialNumber(CertificateSerialNum.getSerialNum());

				if (certificateSerialNumCheck != null) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NO_ALREADY_ISSUED);
					return response;
				}
				certificateDetails.setCertificateSerialNumber(CertificateSerialNum.getSerialNum());
				
				certificateDetails.setInsured(castToString(rowData.getCell(0)));
				certificateDetails.setPolicyNumber(castToString(rowData.getCell(1)));
				
				try {
					certificateDetails.setCommencingDate(DateFormatUtils.getUpoadFormattedDateTime(rowData.getCell(2)));
					certificateDetails.setExpiryDate(DateFormatUtils.getUpoadFormattedDateTime(rowData.getCell(3)));

				} catch (Exception e) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage("Date format should be dd/mm/yyyy");
					return response;
				}

				
				certificateDetails.setRegistartionNumber(castToString(rowData.getCell(4)));
				certificateDetails.setChassisNumber(castToString(rowData.getCell(5)));
				certificateDetails.setVechicleType(castToString(rowData.getCell(6)));
				if (isValidLicencedData(rowData.getCell(7))) {
					certificateDetails.setLicensed(castToString(rowData.getCell(7)));
				} else {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage("Invalid Licensed To Carry Data");
					return response;
				}
				certificateDetails.setUsage(castToString(rowData.getCell(8)));
				
					String emailDataCell = castToString(rowData.getCell(9));
					if(emailDataCell.contains(",")) {
						String[] emailData = emailDataCell.split(",");
						certificateDetails.setPrimaryEmail(emailData[0]);
						StringBuffer secondaryEmail = new StringBuffer();
						for(int i = 1; i<emailData.length; i++) {
							secondaryEmail.append(emailData[i]);
							secondaryEmail.append(",");
						}
						secondaryEmail.deleteCharAt(secondaryEmail.length()-1);
						certificateDetails.setSecondaryEmail(secondaryEmail.toString());
					}else {
						certificateDetails.setPrimaryEmail(emailDataCell);
					}
			
				certificateDetails.setIssuedBy(castToString(rowData.getCell(10)));
				certificateDetails.setApprovedBy(castToString(rowData.getCell(11)));
				certificateDetails.setStatus(CertificateStatus.issued.getStatus());
				certificateDetails.setInsuranceCompany(companyData);
				
				String qrCodeBase64 = getQrBase(certificateDetails.getCertificateSerialNumber());
				certificateDetails.setQrCode(Base64.getDecoder().decode(qrCodeBase64));
				certificateDetails.setUploadedBy(userId);
				certificateDetails.setCreatedDate(LocalDateTime.now());
				certificateDetails.setIssuedDate(LocalDate.now());
				certificateDetails.setBranch(branch);
				
				certificateDetails.setMailStatus("N");
				certificateDetails.setIsOfflineCertificate(0);
				certificateDetailsList.add(certificateDetails);
				CertificateSerialNum.setIssuedStatus("Y");
				certificateSerialNumList.add(CertificateSerialNum);
			}
		}catch (Exception e) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());;
		}
		
		response.setStatus(HttpStatus.OK.value());
		return response;
		
	}


	private void syncOfflineCertificate() {
		
	}

	private String castToString(Cell xssfCell) {
		String value = StringUtils.EMPTY;
		if (xssfCell.getCellTypeEnum() == CellType.STRING) {
			value = xssfCell.getStringCellValue();
		} else if (xssfCell.getCellTypeEnum() == CellType.NUMERIC) {
			value = String.valueOf((int)xssfCell.getNumericCellValue());
		}
		return value;
	}

	private boolean isValidLicencedData(Cell xssfCell) {
		String value = StringUtils.EMPTY;
		if (xssfCell.getCellTypeEnum() == CellType.STRING) {
			value = xssfCell.getStringCellValue();
			if (StringUtils.isNotBlank(value)) {
				double d = Double.parseDouble(value);
				if (d % 1 == 0.0) {
					return true;
				}
			}
		} else if (xssfCell.getCellTypeEnum() == CellType.NUMERIC) {
			double d = xssfCell.getNumericCellValue();
			if (d % 1 == 0.0) {
				return true;	
			}
		}
		return false;
	}

	private boolean rowDataNullCheck(XSSFRow rowData) {
		for(int i= 0; i<rowData.getPhysicalNumberOfCells(); i++) {
		if(rowData.getCell(i) == null) {
			
			return false;
		}
		}
			return true;	
	
		
	}

	private boolean sheetHeaderValidation(Map<Integer, String> headerOrderMap) {

		String[] headerColumn = InsuranceCompanyConstants.ONLINE_XLSX_UPLOAD_CERTIFICATE_HEADER;

		if (headerOrderMap.get(1).equals(InsuranceCompanyConstants.ONLINE_XLSX_UPLOAD_CERTIFICATE_HEADER[0])
				|| headerOrderMap.get(1).equals(InsuranceCompanyConstants.OFFLINE_XLSX_UPLOAD_CERTIFICATE_HEADER[0])) {

			if (headerOrderMap.get(1).equals(InsuranceCompanyConstants.OFFLINE_XLSX_UPLOAD_CERTIFICATE_HEADER[0])) {
				headerColumn = InsuranceCompanyConstants.OFFLINE_XLSX_UPLOAD_CERTIFICATE_HEADER;
			}

			if (headerOrderMap.size() != headerColumn.length) {
				return false;
			}

			for (int index = 1; index < headerColumn.length; index++) {
				if (!headerOrderMap.get(index).equals(headerColumn[index - 1])) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	public Response<String> certificateImageExport(String certificateNo) throws IOException, ParseException {
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateNo);
		if (ObjectUtils.isEmpty(certificateDetails)) {
			throw new BadDataExceptionHandler("Certificate Number is not valid");
		}
		CertificateIssueDto certificateIssueDto = new CertificateIssueDto();
		certificateIssueDto.setCompanyId(certificateDetails.getInsuranceCompany().getCompanyId());
		certificateIssueDto.setCertificateNo(certificateDetails.getCertificateSerialNumber());
		certificateIssueDto.setPolicyHolder(certificateDetails.getInsured());
		certificateIssueDto.setPolicyNumber(certificateDetails.getPolicyNumber());
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		certificateIssueDto.setCommencingDate(DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.getCommencingDate(), true));
		certificateIssueDto.setExpiryDate(sdf.format(certificateDetails.getExpiryDate()));
		certificateIssueDto.setRegistrationNo(certificateDetails.getRegistartionNumber());
		certificateIssueDto.setChassisNo(certificateDetails.getChassisNumber());
		certificateIssueDto.setLicensed(certificateDetails.getLicensed());
		certificateIssueDto.setVehicleType(certificateDetails.getVechicleType());
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		certificateIssueDto.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
		certificateIssueDto.setUserId(user.getUserId());
		certificateIssueDto.setEmail(user.getEmail());
		certificateIssueDto.setUsage(certificateDetails.getUsage());
		certificateIssueDto.setQrCode("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));

		return certificateIssuesUtil.showCertificateByImage(certificateIssueDto);
	}
	
	public void showCertificateByPDF(InsuranceCompany company, CertificateDetails certificateDetails, String email, User user)
			throws ParseException {

		CertificateIssueDto certificateIssueDto = new CertificateIssueDto();
		certificateIssueDto.setCompanyId(company.getCompanyId());
		certificateIssueDto.setCertificateNo(certificateDetails.getCertificateSerialNumber());
		certificateIssueDto.setPolicyHolder(certificateDetails.getInsured());
		certificateIssueDto.setPolicyNumber(certificateDetails.getPolicyNumber());
		
		certificateIssueDto.setCommencingDate(DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.getCommencingDate(), true));
		certificateIssueDto.setExpiryDate(DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.getExpiryDate(), false));
		certificateIssueDto.setRegistrationNo(certificateDetails.getRegistartionNumber());
		certificateIssueDto.setChassisNo(certificateDetails.getChassisNumber());
		certificateIssueDto.setLicensed(certificateDetails.getLicensed());
		certificateIssueDto.setVehicleType(certificateDetails.getVechicleType());
		certificateIssueDto.setUserId(user.getUserId());
		certificateIssueDto.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
		certificateIssueDto.setEmail(email);
		certificateIssueDto.setUsage(certificateDetails.getUsage());
		certificateIssueDto.setQrCode("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));
		try {
			certificateIssuesUtil.showCertificateByPDF(certificateIssueDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Response<String> sendMail(String certificateNo, String email) {
		Response<String> response = new Response<>();
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateNo);
		if (ObjectUtils.isEmpty(certificateDetails)) {
			response.setStatus(200);
			response.setMessage(InsuranceCompanyConstants.CERTIFICATE_NOT_AVAILABLE);
			return response;
		}
		CertificateIssueDto certificateIssueDto = new CertificateIssueDto();
		certificateIssueDto.setCompanyId(certificateDetails.getInsuranceCompany().getCompanyId());
		certificateIssueDto.setCertificateNo(certificateDetails.getCertificateSerialNumber());
		certificateIssueDto.setPolicyHolder(certificateDetails.getInsured());
		certificateIssueDto.setPolicyNumber(certificateDetails.getPolicyNumber());
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		certificateIssueDto.setCommencingDate(DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.getCommencingDate(), true));
		certificateIssueDto.setExpiryDate(sdf.format(certificateDetails.getExpiryDate()));
		certificateIssueDto.setRegistrationNo(certificateDetails.getRegistartionNumber());
		certificateIssueDto.setChassisNo(certificateDetails.getChassisNumber());
		certificateIssueDto.setLicensed(certificateDetails.getLicensed());
		certificateIssueDto.setVehicleType(certificateDetails.getVechicleType());
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		certificateIssueDto.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
		certificateIssueDto.setUserId(user.getUserId());
		certificateIssueDto.setEmail(email);
		certificateIssueDto.setUsage(certificateDetails.getUsage());
		certificateIssueDto.setQrCode("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));

		try {
			certificateIssuesUtil.showCertificateByPDF(certificateIssueDto);
		} catch (ParseException e) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.SOMETHING_WENT_WRONG);
			return response;
		} catch (Exception e) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage(InsuranceCompanyConstants.MAIL_QUOTA_EXCEED);
			return response;
		}
		response.setStatus(HttpStatus.OK.value());
		response.setMessage(InsuranceCompanyConstants.SUCCESSFULLY_CERTIFICATE_SENT);
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
	
	public Response<String> certificateImageShow(String certificateNo) throws IOException, ParseException {
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateNo);
		if (ObjectUtils.isEmpty(certificateDetails)) {
			throw new BadDataExceptionHandler("Certificate Number is not valid");
		}
		CertificateIssueDto certificateIssueDto = new CertificateIssueDto();
		certificateIssueDto.setCompanyId(certificateDetails.getInsuranceCompany().getCompanyId());
		certificateIssueDto.setCertificateNo(certificateDetails.getCertificateSerialNumber());
		certificateIssueDto.setPolicyHolder(certificateDetails.getInsured());
		certificateIssueDto.setPolicyNumber(certificateDetails.getPolicyNumber());
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		certificateIssueDto.setExpiryDate(sdf.format(certificateDetails.getExpiryDate()));
		certificateIssueDto.setCommencingDate(DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.getCommencingDate(), true));
		certificateIssueDto.setRegistrationNo(certificateDetails.getRegistartionNumber());
		certificateIssueDto.setChassisNo(certificateDetails.getChassisNumber());
		certificateIssueDto.setLicensed(certificateDetails.getLicensed());
		certificateIssueDto.setVehicleType(certificateDetails.getVechicleType());
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		certificateIssueDto.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
		certificateIssueDto.setUserId(user.getUserId());
		certificateIssueDto.setEmail(user.getEmail());
		certificateIssueDto.setUsage(certificateDetails.getUsage());
		certificateIssueDto.setQrCode("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));
		String b64 = Base64.getEncoder().encodeToString((imageGenerateService.GenerateImage(certificateDetails)));
		
		//certificateIssueDto.setCertificateImage(b64);
		Response<String> response = new Response<>();
		//return b64;
		
		//return certificateIssuesUtil.showCertificateImage(certificateIssueDto);
		response.setData("data:image/jpeg;base64,"+ b64);

		response.setStatus(200);
		return response;	}
	
	public ByteArrayInputStream certificatePDF(String certificateNo) throws IOException, ParseException {
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateNo);
		if (ObjectUtils.isEmpty(certificateDetails)) {
			throw new BadDataExceptionHandler("Certificate Number is not valid");
		}
		CertificateIssueDto certificateIssueDto = new CertificateIssueDto();
		certificateIssueDto.setCompanyId(certificateDetails.getInsuranceCompany().getCompanyId());
		certificateIssueDto.setCertificateNo(certificateDetails.getCertificateSerialNumber());
		certificateIssueDto.setPolicyHolder(certificateDetails.getInsured());
		certificateIssueDto.setPolicyNumber(certificateDetails.getPolicyNumber());
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		certificateIssueDto.setCommencingDate(DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.getCommencingDate(), true));
		certificateIssueDto.setExpiryDate(sdf.format(certificateDetails.getExpiryDate()));
		certificateIssueDto.setRegistrationNo(certificateDetails.getRegistartionNumber());
		certificateIssueDto.setChassisNo(certificateDetails.getChassisNumber());
		certificateIssueDto.setLicensed(certificateDetails.getLicensed());
		certificateIssueDto.setVehicleType(certificateDetails.getVechicleType());
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		certificateIssueDto.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
		certificateIssueDto.setUserId(user.getUserId());
		certificateIssueDto.setEmail(user.getEmail());
		certificateIssueDto.setUsage(certificateDetails.getUsage());
		certificateIssueDto.setQrCode("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));

		return certificateIssuesUtil.showCertificateByPDFImage(certificateIssueDto);
	}


	public ByteArrayInputStream excelSearchCertificateReport(Integer pageSize, Integer currentPage,Integer companyId, Integer status,
			 String issuedBy, Integer uplodedBy, 
			String startDate, String endDate, List<Integer> branchIds, String transactionDate) throws ParseException, IOException {
		
		InsuranceCompany company = companyRepo.findByCompanyId(companyId);
		 
		if (ObjectUtils.isEmpty(company)) {
			throw new BadDataExceptionHandler(InsuranceCompanyConstants.COMPANY_NOT_FOUND);
		}
		List<BranchMaster> branchList =  new ArrayList<>();
		if (branchIds != null) {
			List<CompanyBranch> companyBranch = companyBranchRepo.findByCompanyBranchIdIn(branchIds);
			for (CompanyBranch branch : companyBranch) {
				branchList.add(branch.getBranch());
			}

		}
		StringBuffer queryString = new StringBuffer("Select certificate from CertificateDetails certificate ");
		queryString.append(" where certificate.insuranceCompany=:company");

		if (issuedBy != null) {
			queryString.append(" AND certificate.issuedBy Like :issuedBy");
		}
		if (status != null) {
			queryString.append(" AND certificate.status=:status");
		}
		if (uplodedBy != null) {
			queryString.append(" AND certificate.uploadedBy=:uplodedBy");
		}
		if (startDate != null && endDate != null) {
			queryString.append(" AND certificate.commencingDate BETWEEN :startDate AND :endDate ");
			queryString.append(" AND certificate.expiryDate BETWEEN :startDate AND :endDate ");
		}

		if (branchIds != null) {

			queryString.append(" AND certificate.branch IN (:branch) ");
		}

		if (transactionDate != null) {
			queryString.append(" AND certificate.createdDate BETWEEN :fromIssedDate AND :toIssedDate ");
		}
		Query query = em.createQuery(queryString.toString());
		query.setParameter("company", company);
		if (issuedBy != null) {
			query.setParameter("issuedBy", "%" + issuedBy + "%");
		}
		if (status != null) {
			query.setParameter("status", status);
		}
		if (uplodedBy != null) {
			query.setParameter("uplodedBy", uplodedBy);
		}
		if (startDate != null && endDate != null) {
			query.setParameter("startDate", DateFormatUtils.formatDateTime(startDate));
			query.setParameter("endDate", DateFormatUtils.formatDateTime(endDate));
		}

		if (branchIds != null) {
			query.setParameter("branch", branchList);
		}
		if (transactionDate != null) {

			query.setParameter("fromIssedDate", LocalDateTime.parse(transactionDate + " 00:00:00",
					DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
			query.setParameter("toIssedDate", LocalDateTime.parse(transactionDate + " 23:59:59",
					DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
		}

		List<CertificateDetails> certificateDetails = query.getResultList();

		String[] COLUMNs =  InsuranceCompanyConstants.ONLINE_XLSX_UPLOAD_CERTIFICATE_SEARCH_REPORT_HEADER;

		    try(
		        Workbook workbook = new XSSFWorkbook();
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ){
		      CreationHelper createHelper = workbook.getCreationHelper();
		      Sheet sheet = workbook.createSheet("certificate");
		     
		      Font headerFont = workbook.createFont();
		      headerFont.setBold(true);
		      headerFont.setColor(IndexedColors.BLUE.getIndex());
		   
		      CellStyle headerCellStyle = workbook.createCellStyle();
		      headerCellStyle.setFont(headerFont);
		      Row headerRow = sheet.createRow(0);
		      for (int col = 0; col < COLUMNs.length; col++) {
		        Cell cell = headerRow.createCell(col);
		        cell.setCellValue(COLUMNs[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet.setColumnWidth(col, 25 * 200);
		       
		        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			      cell.setCellStyle(headerCellStyle);    
		       
		      }
		     
		    if(certificateDetails == null || certificateDetails.size()<=0) {
		    	workbook.write(out);
			    return new ByteArrayInputStream(out.toByteArray());
		    }
		    Cell cell  = null;
		      for (int i = 0; i <certificateDetails.size(); i++) {
		    	  

		    	  Row dataRow = sheet.createRow(i+1);
		    	  if(certificateDetails.get(i).getBranch() != null) {
		    		   cell = dataRow.createCell(0); 
		    		   cell.setCellValue(certificateDetails.get(i).getBranch().getBranchName());
		    	   }else {
		    		   cell = dataRow.createCell(0); 
		    		   cell.setCellValue("NA"); 
		    	   }
		    	   cell = dataRow.createCell(1); 
	    		   cell.setCellValue(certificateDetails.get(i).getCertificateSerialNumber());
	    		   
	    		   cell = dataRow.createCell(2);
		    	   if(certificateDetails.get(i).getStatus() == 1 ) {
		    		   cell.setCellValue("Issued"); 
		    	   }else {
		    		   cell.setCellValue("Revoked");
		    	   }
	    		   
		    	   cell = dataRow.createCell(3); 
		    	   cell.setCellValue(certificateDetails.get(i).getInsured());
		    	   cell = dataRow.createCell(4);
		    	   cell.setCellValue(certificateDetails.get(i).getPolicyNumber());
		    	   
		    	   cell = dataRow.createCell(5);
		    	   String certIssuedDate =  DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.get(i).getCommencingDate(), false);
		    	   cell.setCellValue(certIssuedDate);
		    	  
		    	   cell = dataRow.createCell(6);
		    	   String commencingDateStr =  DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.get(i).getCommencingDate(), false);
		    	   cell.setCellValue(commencingDateStr);
		    	   
		    	   cell = dataRow.createCell(7);
		    	   String expiryDateStr =  DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.get(i).getExpiryDate(), false);
		    	   cell.setCellValue(expiryDateStr);
		    	   
		    	   cell = dataRow.createCell(8);
		    	   cell.setCellValue(certificateDetails.get(i).getVechicleType());
		    	   
		    	   cell = dataRow.createCell(9);
		    	   cell.setCellValue(certificateDetails.get(i).getRegistartionNumber());
		    	   cell = dataRow.createCell(10);
		    	   cell.setCellValue(certificateDetails.get(i).getChassisNumber());
		    	  
		    	   cell = dataRow.createCell(11);
		    	   cell.setCellValue(certificateDetails.get(i).getLicensed());
		    	   cell = dataRow.createCell(12);
		    	   cell.setCellValue(certificateDetails.get(i).getUsage());
		    	   cell = dataRow.createCell(13);
		    	   cell.setCellValue(certificateDetails.get(i).getPrimaryEmail());
		    	   cell = dataRow.createCell(14);
//		    	   User user = userRepo.findByUserId(certificateDetails.get(i).getUploadedBy());
//		    	   cell.setCellValue(getCompanyUserTypeByUserId(user.getUserTypeId()));
		    	   cell.setCellValue(certificateDetails.get(i).getIssuedBy());
		    	   
		    	  
		    	   cell = dataRow.createCell(15);
		    	   cell.setCellValue(certificateDetails.get(i).getApprovedBy());
		    	   
		    	   User user = userRepo.findByUserId(certificateDetails.get(i).getUploadedBy());
		    	   cell = dataRow.createCell(16);
		    	   cell.setCellValue(user.getUserName());
		    	   
		    	   if(certificateDetails.get(i).getStatus() == 1 ) {
		    		   cell = dataRow.createCell(17);
		    		   cell.setCellValue("NA");
		    		   cell = dataRow.createCell(18);
		    		   cell.setCellValue("NA");
		    	   }else {
		    		  CertificateRevoke  certificateRevok = certificateRevokeRepo.findByCertificateDetails(certificateDetails.get(i));
		    		  cell = dataRow.createCell(17);
		    		  String revokeAt = "";
		    		  if (certificateRevok !=null && certificateRevok.getRevokedAt() != null) {
		    		  revokeAt =  DateFormatUtils.convertDBDateFormatToDocFormat(certificateRevok.getRevokedAt(), false);
		    		  }
		    		  cell.setCellValue(revokeAt);
		    		   cell = dataRow.createCell(18);
		    		   String userName = "";
		    		   if (certificateRevok !=null && certificateRevok.getRevokedBy() != null) {
		    		   User userData = userRepo.findByUserId(certificateRevok.getRevokedBy());
		    		   userName = userData.getUserName();
		    		   }
		    		   cell.setCellValue(userName);
		    	   }
			      }
		      workbook.write(out);
		      return new ByteArrayInputStream(out.toByteArray());
		    }
		  }
	
	
	      private String getCompanyUserTypeByUserId(int userTypeId) {
	    	  return  companyUserTypeRepo.findByUserTypeId(userTypeId).getUserType();
	      }
	      
	public Response<RevokeCertificateDTO> getRevokeCertificate(String certificateNo)
			throws IOException, ParseException {
		CertificateDetails certificateDetails = certificateRepo.findByCertificateSerialNumber(certificateNo);
		if (ObjectUtils.isEmpty(certificateDetails)) {
			throw new BadDataExceptionHandler("Certificate Number is not valid");
		}
		CertificateIssueDto certificateIssueDto = new CertificateIssueDto();
		certificateIssueDto.setCompanyId(certificateDetails.getInsuranceCompany().getCompanyId());
		certificateIssueDto.setCertificateNo(certificateDetails.getCertificateSerialNumber());
		certificateIssueDto.setPolicyHolder(certificateDetails.getInsured());
		certificateIssueDto.setPolicyNumber(certificateDetails.getPolicyNumber());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
		certificateIssueDto.setExpiryDate(sdf.format(certificateDetails.getExpiryDate()));
		certificateIssueDto.setCommencingDate(
				DateFormatUtils.convertDBDateFormatToDocFormat(certificateDetails.getCommencingDate(), true));
		certificateIssueDto.setRegistrationNo(certificateDetails.getRegistartionNumber());
		certificateIssueDto.setChassisNo(certificateDetails.getChassisNumber());
		certificateIssueDto.setLicensed(certificateDetails.getLicensed());
		certificateIssueDto.setVehicleType(certificateDetails.getVechicleType());
		User user = userRepo.findByUserId(certificateDetails.getUploadedBy());
		certificateIssueDto.setIssuedBy(getCompanyUserTypeByUserId(user.getUserTypeId()));
		certificateIssueDto.setUserId(user.getUserId());
		certificateIssueDto.setEmail(user.getEmail());
		certificateIssueDto.setUsage(certificateDetails.getUsage());
		certificateIssueDto.setQrCode(
				"data:image/jpeg;base64," + Base64.getEncoder().encodeToString(certificateDetails.getQrCode()));
		String b64 = Base64.getEncoder().encodeToString((imageGenerateService.GenerateImage(certificateDetails)));

		Response<RevokeCertificateDTO> response = new Response<>();

		RevokeCertificateDTO revokeCertificateDTO = new RevokeCertificateDTO();
		revokeCertificateDTO.setImageData("data:image/jpeg;base64," + b64);

		if (certificateDetails.getStatus() == 0) {
			CertificateRevoke certificateRevoke = certificateRevokeRepo.findByCertificateDetails(certificateDetails);
			RevokeReason revokeReason = reasonRepo.findByReason(certificateRevoke.getReason());

			revokeCertificateDTO.setRevokeReasonId(revokeReason.getReasonId());
		}

		response.setData(revokeCertificateDTO);

		response.setStatus(200);
		return response;
	}
	}
	
