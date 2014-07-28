package com.ir.qa.parser.templateParser;
import java.io.IOException;


public class StartDateAndAgeTemplateParser implements TemplateParser {

	@Override
	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		//System.out.println("Inside age parser "+value);
		String result = "";

		
		
			value = value.replaceAll("\\|df=[^\\|]*", "");
			value = value.replaceAll("[\\{]|[\\}]","");
		
		String split[] = value.split("\\|");
		
		if(split.length > 0)
		{
			if(split.length == 4)
			{
				result = split[1]+" "+split[2]+" "+split[3];
				int year = Integer.parseInt(split[1]);
				year = 2013 - year;
				result = TemplateParserUtil.getDate(result);
				result += " "+year+" years ago";
				//System.out.println("Formatted start date:: "+result+" "+year+" years ago");
				//result="";
			}
			if(split.length == 2)
			{
				if(isNumeric(split[1]))
				{
					int year = Integer.parseInt(split[1]);
					int month=0;
					int date=0;
					year = 2013 - year;
					result = year+" years ago";
					if(year == 0)
					{
						month = 12 - Integer.parseInt(split[2]);
						result = month+" months ago";
					}
				}
				else
				{
					String temp[] = split[1].split(" ");
					String month = getMonth(temp[1]);
					int age = 2013 - Integer.parseInt(temp[2]);
					result = temp[2]+" "+month+" "+temp[0];
					result = TemplateParserUtil.getDate(result)+" "+age+" years ago";
					
				}
				//System.out.println("Formatted start date:: "+result);
			}
		}
		
		return result;
	}
	
	boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	String getMonth(String month)
	{
		String[] months={"Summa","January","February","March","April","May","June","July","August","September","October","November","December"};
		String result ="";
		
		for(int i=0;i<months.length;i++)
		{
			if(month.equalsIgnoreCase(months[i]))
			{
				result += i;
				break;
			}
		}
		return result;
	}

}
