package com.ir.qa.parser.templateParser;

import java.io.IOException;

public class SmallTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		String result = "";

		value = value.replaceAll("[\\{]|[\\}]", "");
		String split[] = null;
		// System.out.println("Small:: "+value);
		split = value.split("\\|");

		result = split[1].replaceAll("\\(|\\)", "");
		// System.out.println("Formatted small:: "+result);

		return result;
	}

}
