package com.beyontec.mdcp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {
	
	public static SimpleDateFormat inputSDF = new SimpleDateFormat("dd/mm/yyyy");
	  public static SimpleDateFormat outputSDF = new SimpleDateFormat("yyyy-mm-dd");
	  
		public static SimpleDateFormat inputSDFSQL = new SimpleDateFormat("dd/mm/yyyy");

	  public static SimpleDateFormat outputSDFSQL = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

	  public static Date formatDate(String inDate) throws ParseException {
	    String outDate = "";
	    if (inDate != null) {
	        try {
	            Date date = inputSDF.parse(inDate);
	            outDate = outputSDF.format(date);
	        } catch (ParseException ex){ 
	        }
	    }
	    return outputSDF.parse(outDate);
	  }
	  
	  
	  public static Date formatDateSql(String inDate) throws ParseException {
		    String outDate = "";
		    if (inDate != null) {
		        try {
		            Date date = outputSDFSQL.parse(inDate);
		            outDate = inputSDFSQL.format(date);
		        } catch (ParseException ex){ 
		        }
		    }
		    return inputSDFSQL.parse(outDate);
		  }

	  

}
