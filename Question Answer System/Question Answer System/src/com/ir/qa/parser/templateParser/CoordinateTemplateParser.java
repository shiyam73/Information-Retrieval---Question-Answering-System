package com.ir.qa.parser.templateParser;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinateTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		
		
		String result = "";
		int index = 1;
		value = convert(value);
		int north=0;
		int south=0;
		value = value.replaceAll("}", "");
		String split[];
		 if (value.contains("N") && south!=1) {
			split = value.split("\\|");
			if (split.length > 0) {
				while (!split[index].trim().equalsIgnoreCase("N")) {
					result += split[index] + " ";
					index++;
				}

				if (value.contains("E")) {
					while (!split[index].trim().equalsIgnoreCase("E")) {
						result += split[index] + " ";
						index++;
					}
					result += "E ";
				}
				if (value.contains("W")) {
					while (!split[index].trim().equalsIgnoreCase("W")) {
						result += split[index] + " ";
						index++;
					}
					result += "W ";
				}

			}
			
			north=1;
			// System.out.println("Formatted coordinate:: "+result);
		
		}

		 else if (value.contains("S") && north!=1) {
			split = value.split("\\|");
			if (split.length > 0) {
				while (!split[index].trim().equalsIgnoreCase("S")) {
					result += split[index] + " ";
					index++;
				}
				if (value.contains("E")) {
					while (!split[index].trim().equalsIgnoreCase("E")) {
						result += split[index] + " ";
						index++;
					}
					result += "E ";
				}
				if (value.contains("W")) {
					while (!split[index].trim().equalsIgnoreCase("W")) {
						result += split[index] + " ";
						index++;
					}
					result += "W ";
				}

			}
			
			south=1;
			 //System.out.println("Formatted coordinate:: "+result);
		}
		 else
			{
				split = value.split("\\|");
				if(split.length > 0)
				{
					result += split[1]+" N "+split[2]+" W";
					//System.out.println("Formatted coordinate:: "+result);
					//System.out.println();
					
				}
			}
		return result;
	}
	
	String convert(String coord)
	{
		String result=null;
		result = coord.replaceAll("\\|type:[^\\}]*","");
		result = result.replaceAll("\\|region:[^\\}]*","");
		result = result.replaceAll("\\|display=[^\\}]*","");
		result = result.replaceAll("\\|format=[^\\}]*","");
		result = result.replaceAll("\\|scale:[^\\}]*","");
		//System.out.println("Converted:: "+result);
		return result;
		
	}

}
