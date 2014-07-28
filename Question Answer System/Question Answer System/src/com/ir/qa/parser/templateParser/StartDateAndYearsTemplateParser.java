package com.ir.qa.parser.templateParser;
import java.io.IOException;


public class StartDateAndYearsTemplateParser implements TemplateParser {

	@Override
	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		String result = "";

		
		
		value = value.replaceAll("\\|mf=[^\\|]*", "");
		value = value.replaceAll("\\|df=[^\\|]*", "");
		value = value.replaceAll("\\|br=[^\\|]*", "");
		value = value.replaceAll("[}]", "");
		value = value.replaceAll("[{]", "");
		
		String split[] = value.split("\\|");
		if(split.length > 0)
		{
			if(split.length == 4)
			{
				result = split[1]+" "+split[2]+" "+split[3];
				int year = Integer.parseInt(split[1]);
				year = 2013 - year;
				result = TemplateParserUtil.getDate(result)+" "+year+" years ago";
				//System.out.println("Formatted start date:: "+result+" "+year+" years ago");
				//System.out.println();
				//result="";
			}
			
		}
		return result;
	}

}
