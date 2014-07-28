package com.ir.qa.parser.templateParser;

import java.io.IOException;

public class DurationTemplateParser implements TemplateParser {

	public String parse(String value) throws IOException {

		// TODO Auto-generated method stub

		String result = "";

		value = value.replaceAll("}", "");
		String split[];
		split = value.split("\\|");
		String split1[];

		if (split.length > 0) {
			for (int i = 1; i < 3; i++) {
				split1 = split[i].split("=");
				result += split1[1] + ":";
			}

		}
		return result;
	}

}
