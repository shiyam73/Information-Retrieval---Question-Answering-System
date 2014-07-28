package com.ir.qa.parser.templateParser;

import java.io.IOException;

public class StartDateTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		String result = "";

		value = value.replaceAll("\\|mf=[^\\|]*", "");
		value = value.replaceAll("\\|df=[^\\|]*", "");
		value = value.replaceAll("\\|br=[^\\|]*", "");
		value = value.replaceAll("[}]", "");
		value = value.replaceAll("[{]", "");
		
		String split[] = value.split("\\|");
		if (split.length > 0 && !split[0].contains("age") && !split[0].contains("year")) {
			if (split.length == 2) {
				if(split[1].contains(" "))
				{
					String[] temp = split[1].split(" ");
					split[1] = temp[1];
				}
				result = split[1]+" 01 01";
				result = TemplateParserUtil.getDate(result);
			} else if (split.length == 3) {
				result = split[1]+" "+ split[2]+" 01";
				result = TemplateParserUtil.getDate(result);
			} else if (split.length == 4) {
				result = split[1] + " " + split[2] + " " + split[3];
				//System.out.println("result::"+result);
				result = TemplateParserUtil.getDate(result);
			} else if (split.length == 6) {
				result = split[1] + " " + split[2] + " " + split[3] + " "
						+ split[4]+":"+split[5]+":00";
				result = TemplateParserUtil.getDateTime(result);
			} else if (split.length == 7) {
				result = split[1] + " " + split[2] + " " + split[3] + split[4]+":"+split[5]+":"+split[6];
				result = TemplateParserUtil.getDateTime(result);
			} else if (split.length == 8) {
				result = split[1] + " " + split[2] + " " + split[3] + split[4]+":"+split[5]+":"+split[6];
				result = TemplateParserUtil.getDateTime(result);
				if (split[7].equalsIgnoreCase("z"))
					result += " UTC";
				else
					result += split[7];
			}
		}
		return result;
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