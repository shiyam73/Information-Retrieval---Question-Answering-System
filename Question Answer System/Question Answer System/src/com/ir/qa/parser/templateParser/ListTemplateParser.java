package com.ir.qa.parser.templateParser;

import java.io.IOException;

public class ListTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub

		String result = "";
		int index = 1;

		if(value.contains("Start"))
			value = value.replaceAll("\\| \\(\\{\\{Start[^\\}]+[\\}]{2}","");
		
		/*value = value.replaceAll("\\{\\{|marriage|\\|","");
		value = value.replaceAll("\\|reason=[^\\}]*","");*/
		value = value.replaceAll("}","");
		//System.out.println("SHI:: "+value);
		String split[];
		split = value.split("\\|");
		if (value.contains("Unbulleted") || value.contains("unbulleted")) {
			if (split.length > 0) {

				while (!(split[index].equalsIgnoreCase("class"))
						&& !(split[index].contains("style"))) {

					result += split[index] + ", ";
					index++;
					if (index >= split.length)
						break;
				}
				index = 1;
				// System.out.println("Formatted List:: "+result);
				
			}
		}
		if (value.contains("ubl") || value.contains("plainlist")) {
			if (split.length > 0) {
				if(value.contains("\\*"))
				{
					String split1[] = split[1].split("\\*");
					if (split1.length > 0) {
						for (int i = 1; i < split1.length; i++)
							result += split1[i] + ", ";
					}
				}
				else
				{
				    	for(int i=1;i<split.length;i++)
				    		result += split[i].replaceAll("[{]","")+",";
				    
				}
			}
		}
		result = result.replaceAll("\\*","");
		return result;
	}

}
