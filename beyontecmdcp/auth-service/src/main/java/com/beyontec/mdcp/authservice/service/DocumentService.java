package com.beyontec.mdcp.authservice.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

	public static ByteArrayInputStream customersToExcel() throws IOException {
		Resource resource = new ClassPathResource("/templates/excel/PolicyUpload.xlsx") ;
		return new ByteArrayInputStream(IOUtils.toByteArray(resource.getInputStream()));
	  }
	

	public static ByteArrayInputStream companyUserToExcel() throws IOException {
		 String[] COLUMNs = {"First Name", "Middle Name", "Last Name", 
		    "Mobile Number", "User Name", "Email", "Designation", "Insure Company", "User Type", "Role Id"};
	    try(
	        Workbook workbook = new XSSFWorkbook();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ){
	      CreationHelper createHelper = workbook.getCreationHelper();
	   
	      Sheet sheet = workbook.createSheet("companyuser");
	     
	      Font headerFont = workbook.createFont();
	      headerFont.setBold(true);
	      headerFont.setColor(IndexedColors.BLUE.getIndex());
	   
	      CellStyle headerCellStyle = workbook.createCellStyle();
	      headerCellStyle.setFont(headerFont);
	      
	      
	      headerCellStyle.setDataFormat(
	    	        createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
	      
	     
	      // Row for Header
	      Row headerRow = sheet.createRow(0);
	   
	      // Header
	      for (int col = 0; col < COLUMNs.length; col++) {
	        Cell cell = headerRow.createCell(col);
	        cell.setCellValue(COLUMNs[col]);
	        cell.setCellStyle(headerCellStyle);
	        sheet.setColumnWidth(col, 25 * 200);
	       
	        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
		      cell.setCellStyle(headerCellStyle);
		      
	       
	      }
	    
	     
			/*
			 * // CellStyle for Age CellStyle ageCellStyle = workbook.createCellStyle();
			 * ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));
			 */
	   
	     
	   
	      workbook.write(out);
	      return new ByteArrayInputStream(out.toByteArray());
	    }
	  }


	public ByteArrayInputStream authorityUserToExcel() throws IOException {
		String[] COLUMNs = {"First Name", "Middle Name", "Last Name", 
			    "Mobile Number", "User Name", "Email", "Designation", "Role Id"};
		    try(
		        Workbook workbook = new XSSFWorkbook();
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ){
		      CreationHelper createHelper = workbook.getCreationHelper();
		   
		      Sheet sheet = workbook.createSheet("authorityuser");
		     
		      Font headerFont = workbook.createFont();
		      headerFont.setBold(true);
		      headerFont.setColor(IndexedColors.BLUE.getIndex());
		   
		      CellStyle headerCellStyle = workbook.createCellStyle();
		      headerCellStyle.setFont(headerFont);
		      
		      
		      headerCellStyle.setDataFormat(
		    	        createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
		      
		     
		      // Row for Header
		      Row headerRow = sheet.createRow(0);
		   
		      // Header
		      for (int col = 0; col < COLUMNs.length; col++) {
		        Cell cell = headerRow.createCell(col);
		        cell.setCellValue(COLUMNs[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet.setColumnWidth(col, 25 * 200);
		       
		        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
			      cell.setCellStyle(headerCellStyle);
			      
		       
		      }
		    
		     
				/*
				 * // CellStyle for Age CellStyle ageCellStyle = workbook.createCellStyle();
				 * ageCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#"));
				 */
		   
		     
		   
		      workbook.write(out);
		      return new ByteArrayInputStream(out.toByteArray());
		    }
		  }

	
}