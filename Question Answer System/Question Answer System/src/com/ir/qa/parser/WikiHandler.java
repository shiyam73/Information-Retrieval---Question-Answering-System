package com.ir.qa.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WikiHandler extends DefaultHandler {
	Logger logger = Logger.getLogger(WikiHandler.class.getName());
	Stack<String> nodeStack = new Stack<String>();
	String timeStamp = null;
	String title = null;
	String username = null;
	long id = 0;
	static Integer count = 0;
	StringBuilder sb = new StringBuilder();
	StringBuilder infobox = null;
	Collection<WikipediaDocument> docs = null;
	WikipediaParser wikiParser = null;
	boolean text = false;
	boolean infoStarts = false;
	int braceCount = 0;
	static int counter = 0;
	static int typeCount = 0;
	private static HashMap<String, Integer> collectingTypes = new HashMap<String, Integer>();

	Pattern p = Pattern.compile("[\\{]{2}Infobox");
	Pattern p1 = Pattern.compile("[\\{]");
	Pattern p2 = Pattern.compile("[\\}]");
	Pattern p3 = Pattern.compile("[\\{]{2}Infobox([^|]*)|(.*)$");
	Pattern p4 = Pattern.compile("[\\}\\{]");
	Matcher m = null;
	Matcher m1 = null;
	Matcher m2 = null;
	Matcher m3 = null;
	Matcher m4 = null;
	HashMap<String, String> map = new HashMap<String, String>();
	Map<String, Integer> typeSet = new TreeMap<String, Integer>();

	public WikiHandler(Collection<WikipediaDocument> docs) {
		this.docs = docs;
		File f = new File("files"+File.separator+"InfoboxTypes.txt");
		// File f = new File();

		// WikiDocGenerator wikiDoc = new WikiDocGenerator(docs);
		FileReader fr = null;
		BufferedReader br = null;
		String line = null;

		if (collectingTypes.isEmpty()) {
			try {
				if (f.exists()) {
					// System.out.println("File exists");
					fr = new FileReader(f);
					br = new BufferedReader(fr);

					while ((line = br.readLine()) != null) {
						line = line.toLowerCase().trim();
						if (line.equalsIgnoreCase("")) {
							typeCount++;
						}

						collectingTypes.put(line.trim(), typeCount);
					}
					// System.out.println("REading line");
				}
			} catch (Exception e) {
				//e.printStackTrace();
			} finally {
				try {
					if (fr != null) {
						fr.close();
					}
					if (br != null) {
						br.close();
					}
				} catch (Exception e) {

				}
			}
		}
	}

	public void startDocument() {
		// logger.log(Level.INFO,"Document Parsing Started");
	}

	public void endDocument() {
		// logger.log(Level.INFO,"Document parsing completed");
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		nodeStack.push(qName);
		if ("text".equalsIgnoreCase(qName)) {
			text = true;
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		try {
			// logger.log(Level.FINE,"URI::" + uri);
			// logger.log(Level.FINE,"Local Name::" + localName);
			// logger.log(Level.FINE,"End Element :" + qName);
			// logger.log(Level.FINE,"Element Contents : " + sb.toString());
			nodeStack.pop();
			if ("timestamp".equalsIgnoreCase(qName)) {
				timeStamp = sb.toString().trim();
			}
			if ("title".equalsIgnoreCase(qName)) {
				title = sb.toString().trim();
			}
			if ("username".equalsIgnoreCase(qName)
					|| "ip".equalsIgnoreCase(qName)) {
				username = sb.toString().trim();
			}
			if ("id".equalsIgnoreCase(qName)) {
				String parent = nodeStack.peek();
				if ("page".equalsIgnoreCase(parent)) {
					id = Long.parseLong(sb.toString().trim());
				}
			}
			if ("text".equalsIgnoreCase(qName)) {
				// logger.log(Level.INFO,"Creating a wiki document");
				// logger.log(Level.INFO,"WikiDocument ID::" +id);
				// logger.log(Level.INFO,"WikiDocument Timestamp::"+timeStamp);
				// logger.log(Level.INFO,"WikiDocument Username::"+username);
				// logger.log(Level.INFO,"WikiDocument Title::"+title);
				// TODO :: Need to add sections , categories , links and
				// language links
				// wikiParser = new
				// WikipediaParser(sb.toString().trim(),wikiDoc);
				// wikiParser.parse();
				if (infobox != null && infobox.toString().trim() != "") {
					WikipediaDocument wikiDoc = new WikipediaDocument(id,
							timeStamp, username, title);
					// System.out.println("Count::" + count);
					// System.out.println("Title::"+title);
					wikiParser = new WikipediaParser(infobox.toString().trim(),
							wikiDoc);
					docs.add(wikiDoc);
					count++;
					if (infoStarts || braceCount != 0) {
						System.out.println("Exception::" + infobox);
						infoStarts = false;
						braceCount = 0;
					}
					infobox = null;
				}
				// System.out.println(sb.toString());
				synchronized (docs) {
					if (docs.size() > 50) {
						Thread.sleep(500);
					}
				}
				text = false;
				counter++;
				// System.out.println("Counter::" + counter);
				// System.out.println("Title::" + title);
				// logger.log(Level.INFO,"Added the Wiki Document to Docs Collection");
			}
			// System.out.println("Text::" + text + "qname::" + qName);
			sb = new StringBuilder();
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {

		if (text) {
			String xmlRetrieve = new String(ch, start, length);
			String[] lines = xmlRetrieve.split("\n");
			for (int j = 0; j < lines.length; j++) {

				String line = lines[j].trim();
				line = line.replaceAll("&gt;", ">");
				line = line.replaceAll("&lt;", "<");
				line = line.replaceAll("&quot;", "'");
				line = line.replaceAll("&amp;", "&");
				line = line.replaceAll("&lt;", "<");
				line = line.replaceAll("&nbsp;", "");
				line = line.replaceAll("&uml;", "");
				line = line.replaceAll("&copy;", "");
				line = line.replaceAll("&para;", "'");
				line = line.replaceAll("&middot;", ">");
				line = line.replaceAll("&cedil;", "<");
				line = line.replaceAll("&sup1;", "'");
				line = line.replaceAll("&not;", "'");
				line = line.replaceAll("&not;", ">");
				line = line.replaceAll("&reg;", "<");
				line = line.replaceAll("&macr;", "'");

				if (infoStarts) {
					// m1 = p1.matcher(line);
					// System.out.println(line);
					m4 = p4.matcher(line);
					while (m4.find()) {
						if (line.substring(m4.start(), m4.end())
								.equalsIgnoreCase("{")) {
							braceCount++;
						} else {
							braceCount--;
						}
						// System.out.println("BraceCount::"+braceCount);
						if (braceCount == 0) {
							infobox.append(line.substring(0, m4.end()));
							infoStarts = false;
							break;
						}
					}
					infobox.append(line);

					/*
					 * while (m1.find()) { braceCount++; } //
					 * System.out.println("line::" + line); m2 =
					 * p2.matcher(line); while (m2.find()) { braceCount--; //
					 * System.out.println("End Brace:: " + braceCount);
					 * 
					 * } infobox.append(line); if (braceCount == 0) { //
					 * System.out.println("Final Text::" // +sb.toString()); //
					 * System.out.println("brace::"+line); infoStarts = false;
					 * // System.out.println("Info ends"); // } } }else{
					 * 
					 * }
					 */
				} else {
					m = p.matcher(line);

					if (m.find()) {
						// System.out.println("Infostarts");
						// System.out.println("Infoline::" + line +
						// "Bracecount::"
						// + braceCount);

						if (!infoStarts) {
							// braceCount++;
							// braceCount++;
							// System.out.println("Braces::" + braceCount);
						}

						line = line.replaceAll("(.*)\\{\\{Infobox",
								"\\{\\{Infobox");
						m3 = p3.matcher(line);
						if (m3.find()) {
							String type = m3.group(1).toString();
							// System.out.println("TYPE:: "+type); type =
							type = type.replaceAll("[<]!+([^\\<\\>]*)[>]", " ");
							type = type.replaceAll("}}", " "); //
							// System.out.println("user type::" + type);
							type = type.trim().toLowerCase();
							if (collectingTypes.containsKey(type)) {
								if (!typeSet.containsKey(type)) {
									typeSet.put(type, 1);
								} else {
									Integer count = typeSet.get(type);
									typeSet.put(type, ++count);
								}
								StringBuilder typeSb = new StringBuilder();
								typeSb.append("infobox_type");
								typeSb.append("=");
								typeSb.append(collectingTypes.get(type));
								// typeSb.append("|");
								// System.out.println(typeSb.toString());
								// System.out.println("Line :: " + line);

								/*
								 * m1 = p1.matcher(line); while (m1.find()) {
								 * braceCount++; //
								 * System.out.println("New Brace:: " + //
								 * braceCount); } // System.out.println("line::"
								 * + line); m2 = p2.matcher(line); while
								 * (m2.find()) { braceCount--; }
								 */
								line = line.replaceAll("[\\{]{2}Infobox[^|]*",
										typeSb.toString());
								// System.out.println("Line :: " + line);
								braceCount++;
								braceCount++;
								infobox = new StringBuilder();

								m4 = p4.matcher(line);
								while (m4.find()) {
									if (line.substring(m4.start(), m4.end())
											.equalsIgnoreCase("{")) {
										braceCount++;
									} else {
										braceCount--;
									}

									if (braceCount == 0) {
										infobox.append(line.substring(0,
												m4.end()));
										infoStarts = false;
										break;
									}
								}
								infobox.append(line);
								infoStarts = true;

								/*
								 * m1 = p1.matcher(line); while (m1.find()) {
								 * braceCount++; //
								 * System.out.println("New Brace:: " + //
								 * braceCount); } // System.out.println("line::"
								 * + line); m2 = p2.matcher(line); while
								 * (m2.find()) { braceCount--; }
								 */

							} else {
								braceCount = 0;
							}
						}
					}
				}
			}
		} else {
			sb.append(ch, start, length);
			// System.out.println("sb::"+sb);
		}
	}
}