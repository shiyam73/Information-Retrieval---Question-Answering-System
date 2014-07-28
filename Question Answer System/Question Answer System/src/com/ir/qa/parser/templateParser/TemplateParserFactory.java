package com.ir.qa.parser.templateParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParserFactory {

	private static Pattern agePattern = Pattern.compile("[\\{]{2}Age in years and days[^\\}]+[\\}]{2}");
	private static Pattern birthPattern = Pattern
			.compile("[\\{]{2}Birth[^\\}]+[\\}]{2}|[\\{]{2}birth[^\\}]+[\\}]{2}");
	private static Pattern coordPattern = Pattern
			.compile("[\\{]{2}coord[^\\}]+[\\}]{2}|[\\{]{2}Coord[^\\}]+[\\}]{2}");
	private static Pattern deathPattern = Pattern
			.compile("[\\{]{2}Death[^\\}]+[\\}]{2}|[\\{]{2}death[^\\}]+[\\}]{2}");
	private static Pattern durationPattern = Pattern
			.compile("[\\{]{2}Duration[\\|0-9ms\\=]+[\\}]{2}");
	private static Pattern ublPattern = Pattern
			.compile("[\\{]{2}Unbulleted list[^\\}]+[\\}]{2}|[\\{]{2}ubl[^\\}]+[\\}]{2}|[\\{]{2}plainlist[^\\}]+[\\}]{2}");
	private static Pattern marriagePattern = Pattern.compile("[\\{]{2}marriage[^\\}]+[\\}]{2}");
	private static Pattern expPattern = Pattern
			.compile("[\\{]{2}e[\\|0-9\\&\\-]+[\\}]{2}|[\\{]{2}E[\\|0-9]+[\\}]{2}");
	private static Pattern smallPattern = Pattern.compile("[\\{]{2}small[^\\}]+[\\}]{2}");
	private static Pattern startDatePattern = Pattern
			.compile("[\\{]{2}Start date[^\\}]+[\\}]{2}");
	private static Pattern startDateAndAgePattern = Pattern
			.compile("[\\{]{2}Start date and age[^\\}]+[\\}]{2}");
	private static Pattern startDateAndYearPattern = Pattern
			.compile("[\\{]{2}Start date and years ago[^\\}]+[\\}]{2}");
	private static Pattern urlPattern = Pattern.compile("[\\{]{2}URL[^\\}]+[\\}]{2}");
	private static Pattern flagPattern = Pattern.compile("[\\{]{2}flag[^\\}]+[\\}]{2}");
	private static Pattern countryPattern = Pattern.compile("[\\{]{2}[A-Za-z]{1,3}[\\}]{2}");
	private static Pattern birthDeathAge = Pattern.compile("[\\{]{2}BirthDeathAge[^\\}]+[\\}]{2}");

	public static TemplateParser getTemplateParser(String matchedString) {
		//System.out.println("Entered getTemplateParser factory");
		TemplateParser templateParser = null;
		Matcher m = birthPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new BirthDateTemplateParser();
			//System.out.println("1");
		}
		m = agePattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new AgeTemplateParser();
			//System.out.println("2");
		}
		m = coordPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new CoordinateTemplateParser();
			//System.out.println("3");
		}
		m = deathPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new DeathDateTemplateParser();
			//System.out.println("4");
		}
		m = durationPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new DurationTemplateParser();
			//System.out.println("5");
		}
		m = expPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new PowerTemplateParser();
			//System.out.println("6");
		}
		m = ublPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new ListTemplateParser();
			//System.out.println("7");
		}
		m = startDatePattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new StartDateTemplateParser();
			//System.out.println("8");
		}
		m = startDateAndAgePattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new StartDateAndAgeTemplateParser();
			//System.out.println("9");
		}
		m = startDateAndYearPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new StartDateAndYearsTemplateParser();
			//System.out.println("10");
		}
		m = marriagePattern.matcher(matchedString);
		if(m.find()){
			templateParser = new MarriageTemplateParser();
			//System.out.println("11");
		}
		m = smallPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new SmallTemplateParser();
			//System.out.println("12");
		}
		m = urlPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new URLTemplateParser();
			//System.out.println("13");
		}
		m = flagPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new MediaWikiTemplateParser();
			//System.out.println("14");
		}
		m = countryPattern.matcher(matchedString);
		if (m.find()) {
			templateParser = new MediaWikiTemplateParser();
			//System.out.println("15");
		}
		m = birthDeathAge.matcher(matchedString);
		if (m.find()) {
			templateParser = new BirthDeathAgeTemplateParser();
			//System.out.println("16");
		}
		return templateParser;
	}
}
