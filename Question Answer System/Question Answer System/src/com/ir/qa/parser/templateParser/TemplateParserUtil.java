package com.ir.qa.parser.templateParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TemplateParserUtil {

	public static String getDate(String value){
		String fullDate = null;
		try{
		DateFormat rawDateFormat = new SimpleDateFormat("yyyy MM dd");
		Date dateObj = rawDateFormat.parse(value);
		//Date dateObj = new Date(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(date));
		DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		fullDate = df.format(dateObj);
		}catch(Exception e){
			e.printStackTrace();
		}
		return fullDate;
	}
	
	public static String getDateTime(String value){
		String fullDate = null;
		try{
		DateFormat rawDateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
		Date dateObj = rawDateFormat.parse(value);
		//Date dateObj = new Date(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(date));
		DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		fullDate = df.format(dateObj);
		}catch(Exception e){
			e.printStackTrace();
		}
		return fullDate;
	}
}