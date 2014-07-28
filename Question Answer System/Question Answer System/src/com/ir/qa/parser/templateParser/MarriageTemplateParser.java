package com.ir.qa.parser.templateParser;

import java.io.IOException;

public class MarriageTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {
		String result = "";

		// TODO Auto-generated method stub
		value = value.replaceAll("\\|reason=[^\\}]*", "");
		value = value.replaceAll("[\\{]|[\\}]", "");
		// System.out.println("Marriage template:: "+value);
		String split[] = value.split("\\|");
		String temp = "";

		if (split.length > 0) {
			if(split.length == 3)
			{
				result = split[1]+" (m. ";
				temp = split[2];
				if(temp.contains(","))
					temp = change(temp);
				result += temp+")";
				//System.out.println("Formatted Marriage template:: "+result);
				
			}
			if(split.length == 4)
			{
				result = split[1]+" (m. "+split[2]+"-"+split[3]+")";
				
				//System.out.println("Formatted Marriage template:: "+result);
				
			}
			if(split.length == 5)
			{
				result = split[1]+" (m. "+split[3]+"-"+split[4]+")";
				
				//System.out.println("Formatted Marriage template:: "+result);
				
			}
		}
		return result;
	}

	String change(String txt) {
		String[] split = txt.split(",");
		return split[1];
	}
}