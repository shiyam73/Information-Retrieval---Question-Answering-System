package com.ir.qa.parser;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ir.qa.parser.templateParser.TemplateParser;
import com.ir.qa.parser.templateParser.TemplateParserFactory;



public class WikipediaParser {

	private static Pattern p5 = Pattern.compile("\\{\\{([^\\}]*)\\}\\}");
	// private static Pattern p6 = Pattern
	// .compile("[\\{]{2}Birth[\\|0-9a-z\\= ]+[\\}]{2}|[\\{]{2}Death[\\|0-9a-z\\= ]+[\\}]{2}|[\\{]{2}birth[\\|0-9a-z\\= ]+[\\}]{2}|[\\{]{2}death[\\|0-9a-z\\= ]+[\\}]{2}");

	// Pattern p1 = Pattern.compile("[\\{]{2}");
	// Pattern p2 = Pattern.compile("[\\}]{2}");
	private boolean change = false;

	public WikipediaParser(String content, WikipediaDocument wikiDoc) {
		// TODO Auto-generated constructor stub

		// System.out.println("Conntent:: "+content);
		try{
			//System.out.println("Content:: "+content);
		content = content.replaceAll("[<]!+([^\\<\\>]*)[>]", " ");
		content = content.replaceAll("[<]br[\\s]*[\\/]*[>]", ",");
		content = content
				.replaceAll("[<]ref[^<>]*[>][^<>]*[<][\\/]ref[>]", " ");
		content = content
				.replaceAll("[<]small[^<>]*[>][^<>]*[<][\\/]small[>]", " ");

		content = content.replaceAll("[\\<][^\\<\\>]*[\\>]", " ");
	//	content = content
		//		.replaceAll(
			//			"[<][\\;][a-zA-z 0-9 \\s \\= \\' \\/ \\\" \\& \\- \\! \\: \\] \\[ \\*]*[>][\\;]",
				//		" ");
	//	content = content.replaceAll("[\\[]{2}|[\\]]{2}", "");
		
		content = parseLinks(content);
		
		content = parseTextFormatting(content);
		//System.out.println("title::" + wikiDoc.getId());
		//System.out.println("Content::"+content);
		Matcher m5 = p5.matcher(content);
		StringBuffer sb = new StringBuffer();
		while (m5.find()) {
			change =true;
			//System.out.println("Group::" + m5.group(1).toString());
			TemplateParser tp = TemplateParserFactory.getTemplateParser("{{"
					+ m5.group(1).toString() + "}}");
			
			try {
				if (tp != null) {
			//		System.out.println("{{" + m5.group(1).toString() + "}}");
					String parsedString = tp.parse("{{" + m5.group(1).toString() + "}}");
			//		System.out.println(parsedString);
					m5.appendReplacement(sb, parsedString);
				}else{
					m5.appendReplacement(sb,"");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// System.out.println("Exception ");
				// System.out.println(m5.group(1).toString());
			//	System.out.println("Exception :: {{" + m5.group(1).toString() + "}}");
			//	e.printStackTrace();
			}
		}
		m5.appendTail(sb);
		if(change){
			content = sb.toString();
		}
	//	System.out.println("Replaced::"+content);
		
		wikiDoc.addInfoboxContent(content);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String parseTextFormatting(String text) {
		
		if(text == null)
			return null;
		if("".equalsIgnoreCase(text))
			return "";
		
		String s = text.replaceAll("[']{2,3}|[']{5}", "").trim();
			//	s.trim();
		return s;
		}
	public static String parseLinks(String text)
	{
		text = text.replaceAll("\\[|[\\|][A-Za-z 0-9]+[\\]]{2}", "").replaceAll("\\]", "");
		return text;
	}
}
