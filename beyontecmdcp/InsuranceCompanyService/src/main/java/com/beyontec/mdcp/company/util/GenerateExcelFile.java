package com.beyontec.mdcp.company.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

public class GenerateExcelFile {

	private HSSFWorkbook workbook = null;
	private HSSFSheet sheet = null;
	private FileOutputStream fileOut = null;
	String filename = InsuranceCompanyConstants.OFFLINE_CERTIFICATE_EXCEL_STORED_LOCATION;

	public GenerateExcelFile() {
		workbook = new HSSFWorkbook();
		sheet = workbook.createSheet("Certificate");
	}

	public void createOfflineCertificateXlsx(List<String> listOfOfflineCertificates, String companyName) {

		createHeaderData();
		populateCertificateDetailsInXlsx(listOfOfflineCertificates, companyName);
		saveFile();
	}

	private void createHeaderData() {
		String[] offlineCertificateHeader = InsuranceCompanyConstants.OFFLINE_XLSX_UPLOAD_CERTIFICATE_HEADER;

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.BLUE.getIndex());
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);

		HSSFRow rowhead = sheet.createRow((short) 0);
		for (int i = 0; i < offlineCertificateHeader.length; i++) {
			rowhead.createCell(i).setCellStyle(headerCellStyle);
			rowhead.createCell(i).setCellValue(offlineCertificateHeader[i]);
		}
	}

	private void populateCertificateDetailsInXlsx(List<String> listOfOfflineCertificates, String companyName) {
		for (int i = 0; i < listOfOfflineCertificates.size(); i++) {
			HSSFRow row = sheet.createRow((short) (i + 1));

			row.createCell(0).setCellValue(listOfOfflineCertificates.get(i));
			row.createCell(13).setCellValue(companyName);
			row.createCell(14).setCellValue(InsuranceCompanyConstants.NOT_ISSUED);
		}
	}

	private void saveFile() {
		try {
			fileOut = new FileOutputStream(filename);
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
