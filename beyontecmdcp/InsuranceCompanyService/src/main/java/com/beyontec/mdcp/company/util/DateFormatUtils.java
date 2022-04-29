package com.beyontec.mdcp.company.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class DateFormatUtils {

	public static SimpleDateFormat inputSDF = new SimpleDateFormat("dd/MM/yyyy");
	public static SimpleDateFormat inputSDF2 = new SimpleDateFormat("dd-MM-yyyy");
	public static SimpleDateFormat outputSDF = new SimpleDateFormat("yyyy-MM-dd");

	public static SimpleDateFormat inputSDFTime = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	public static SimpleDateFormat outputSDFTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public static SimpleDateFormat commencingDateFromFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	public static SimpleDateFormat commencingDateFromFormat2 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	public static SimpleDateFormat commencingDateToFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static SimpleDateFormat commencingDateToFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static Date formatDate(String inDate) throws ParseException {
		String outDate = "";
		if (inDate != null) {
			try {
				Date date = inputSDF.parse(inDate);
				outDate = outputSDF.format(date);
			} catch (ParseException ex) {
			}
		}
		return outputSDF.parse(outDate);
	}

	public static Date formatDateTime(String inDate) throws ParseException {
		String outDate = "";
		if (inDate != null) {
			try {
				Date date = inputSDFTime.parse(inDate);
				outDate = outputSDFTime.format(date);
			} catch (ParseException ex) {
			}
		}
		return outputSDFTime.parse(outDate);
	}

	public static Date getFormattedDateTime(String dateAndTime) throws ParseException {
		if (!StringUtils.isBlank(dateAndTime)) {
			try {
				return commencingDateToFormat
						.parse(commencingDateToFormatter.format(commencingDateFromFormat.parse(dateAndTime)));
			} catch (ParseException ex) {
				try {
					Date date = inputSDF.parse(dateAndTime);
					return outputSDF.parse(outputSDF.format(date));
				} catch (ParseException ex1) {
					throw ex1;
				}
			}
		}
		return null;
	}
	
	public static String convertDBDateFormatToDocFormat(Date dateAndTime, boolean isCommencingDateFormat) {
        try {
        	String dateStr = commencingDateToFormatter.format(dateAndTime);
			Date date =commencingDateToFormatter.parse(dateStr);
			
			if(isCommencingDateFormat)
			   return commencingDateFromFormat.format(date);
			else
			   return inputSDF.format(date);
		} catch (ParseException e) {
		}
		return null;
}
	
	public static Date getUpoadFormattedDateTime(Cell xssfCell) throws Exception {
		try {
			if (xssfCell.getCellTypeEnum() == CellType.STRING) {
				return getUpoadStringFormattedDateTime(xssfCell);
			} else if (xssfCell.getCellTypeEnum() == CellType.NUMERIC) {

				Date dt = xssfCell.getDateCellValue();
				Calendar cal = Calendar.getInstance();
				cal.setTime(dt);
				int month = cal.get(Calendar.MONTH);
				int D1 = cal.get(Calendar.DAY_OF_MONTH);
				Calendar c = Calendar.getInstance();
				c.set(Calendar.YEAR, cal.get(Calendar.YEAR));
				c.set(Calendar.MONTH, D1 - 1);
				c.set(Calendar.DAY_OF_MONTH, month + 1);
				c.set(Calendar.HOUR, cal.get(Calendar.HOUR));
				c.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
				Date d = c.getTime();
				return commencingDateToFormat.parse(commencingDateToFormatter.format(d));
			}
			
		} catch (Exception ex) {
			throw ex;
		}
		return null;
	}
	
	public static Date getUpoadStringFormattedDateTime(Cell xssfCell) throws Exception {
		String dateAndTime = xssfCell.toString();
		if (!StringUtils.isBlank(dateAndTime)) {
			Date date= null;
			String [] dateHMArr = {"dd/MM/yyyy HH:mm","dd-MM-yyyy HH:mm"};
			String [] dateArr = {"dd/MM/yyyy","dd-MM-yyyy","dd-MMM-yyyy","dd/MMM/yyyy"};
			String dtArr[] = dateAndTime.split("/");
			if (dtArr.length > 2) {
				int month = Integer.parseInt(dtArr[1]);
				if (month > 12)
					throw new Exception();
				int day = Integer.parseInt(dtArr[0]);
				int year = Integer.parseInt(dtArr[2].split(" ")[0]);
				if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
						&& day > 31)
					throw new Exception();
				else if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30)
					throw new Exception();
				else if ( month == 2 && year / 4 == 0 && day > 29)
					throw new Exception();
				else if ( month == 2 && year / 4 != 0 && day > 28)
					throw new Exception();
			}
			for (String dateFmt : dateHMArr) {
				try {
					date = new SimpleDateFormat(dateFmt).parse(dateAndTime);
					if (date != null) {
						return commencingDateToFormat
								.parse(commencingDateToFormatter.format(date));
					}
				} catch (ParseException ex) {
					ex.printStackTrace();
				}
			}
			for (String dateFmt : dateArr) {
				try {
					date = new SimpleDateFormat(dateFmt).parse(dateAndTime);
					if (date != null) {
						return outputSDF.parse(outputSDF.format(date));
					}
				} catch (ParseException ex) {
					ex.printStackTrace();
				}

			}
			if (date == null) {
				throw new Exception();
			}
		}
		return null;
	}
}
