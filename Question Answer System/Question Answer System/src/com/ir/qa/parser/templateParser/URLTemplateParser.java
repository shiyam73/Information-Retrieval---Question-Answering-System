package com.ir.qa.parser.templateParser;

import java.io.IOException;

public class URLTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		String result = "";
		value = value.replaceAll("[\\{]|[\\}]", "");
		String split[] = null;
		// System.out.println("URL:: "+value);
		split = value.split("\\|");

		result = split[1].replaceAll("http://", "");
		return result;
	}

}
