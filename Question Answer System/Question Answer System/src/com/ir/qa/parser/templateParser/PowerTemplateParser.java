package com.ir.qa.parser.templateParser;

import java.io.IOException;

public class PowerTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {
		// TODO Auto-generated method stub
		String result = "";

		String split[] = value.split("\\|");
		if (split.length > 0) {
			split[1] = split[1].replaceAll("&", "");
			result = "10^" + split[1].replaceAll("}", "");
		}
		return result;
	}
}
