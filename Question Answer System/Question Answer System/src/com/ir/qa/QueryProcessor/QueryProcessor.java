package com.ir.qa.QueryProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.apache.solr.client.solrj.SolrServerException;

public class QueryProcessor {

	private static NamedEntityRecognition demo = null;
	private static LinguisticProcessor nlpClass = null;
	private static SolrJSearcher searcher = null;
	public static HashSet<String> fieldTypes = new HashSet<String>();
	private static WordNetDemo d = null;
	public static Map<String, String> relWords = new TreeMap<String, String>();

	public static void initialize() {
		try {
			System.out.println("Initialization started");
			System.out.print(System.getProperty("server.home"));
			File f = new File(System.getProperty("server.home")+File.separator+"files"+File.separator+"FieldTypes.txt");
			// File f = new File();

			// WikiDocGenerator wikiDoc = new WikiDocGenerator(docs);
			FileReader fr = null;
			BufferedReader br = null;

			String line = null;

			if (fieldTypes.isEmpty()) {
				try {
					if (f.exists()) {
						// System.out.println("File exists");
						fr = new FileReader(f);
						br = new BufferedReader(fr);

						while ((line = br.readLine()) != null) {
							line = line.toLowerCase().trim();
							fieldTypes.add(line.trim());
						}
						// System.out.println("REading line");
					}
				} catch (Exception e) {
					e.printStackTrace();
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
				System.out.println("fields List::" + fieldTypes);
			}
			demo = new NamedEntityRecognition();
			nlpClass = new LinguisticProcessor();
			searcher = new SolrJSearcher();
			searcher.setFieldNames(fieldTypes);
			searcher.initializeFieldNameCollection(fieldTypes);
			d = new WordNetDemo();
			relWords = d.demoWordNet(fieldTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void fileQuery() {
		File f = new File("Query.txt");
		// File f = new File();

		// WikiDocGenerator wikiDoc = new WikiDocGenerator(docs);
		FileReader fr = null;
		BufferedReader br = null;
		String line = null;

		try {
			if (f.exists()) {
				// System.out.println("File exists");
				fr = new FileReader(f);
				br = new BufferedReader(fr);

				while ((line = br.readLine()) != null) {
					line = line.trim();
					System.out.println("Query ::" + line);
					query(line);

				}
				// System.out.println("REading line");
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	public static void queryInput() {
		try {
			System.out.println("Enter the question:" + "\n");
			Scanner input = new Scanner(System.in);
			String question = input.nextLine();
			query(question);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String,Map<String,String>> query(String question) throws Exception {

		// System.out.println("Enter the question:"+"\n");
		// Scanner input=new Scanner(System.in);
		// String question= input.nextLine();
		String entityType = null;
		String entityKeyword = null;
		String questionType = null;
		String adjective = "";
		String proper_Nouns = "";
		String adjectiveType = null;
		String verb = "";
		String verbType = null;
		String noun = "";
		String caseChanged_string=null;
		// System.out.println(relWords);
		try {
			/*String questionNew=WordUtils.capitalizeFully(question, new char[]{' '});
			questionNew.replaceAll(" ", "");
			System.out.println("Modified Beginning Question" +			questionNew);
			LinkedList<POS> posList = nlpClass.findPOS(questionNew);*/
			LinkedList<POS> posList = nlpClass.findPOS(question);
			  for(POS pos:posList) { System.out.println(pos.getPosType());
			  System.out.println(pos.getPosValue()); }
			
			LinkedList<Entity> entityList = demo.findEntity(question);
			System.out.println("My size is" +entityList.size());
			
			
			/* for(POS pos:posList) { 
				 
				 pos.setPosValue(pos.getPosValue().toLowerCase());	
			 }*/
			//Handle Date
			if(entityList.size()==0)
			{   int flag=0;
				final Pattern linkPattern = Pattern.compile("\\d{1,2}-[a-zA-Z]{3}-\\d{4}"); 
				Matcher sectionMatcher = linkPattern.matcher(question);
				System.out.println("Inside 1");
				List<Integer>countPosn = new ArrayList<Integer>();
				while(sectionMatcher.find()) {
					System.out.println("Inside 2");
					countPosn.add(sectionMatcher.start());
					countPosn.add(sectionMatcher.end());
				    flag=1;
				}
				 if(flag==1)
				 { 
				  String extracted_date=question.substring(countPosn.get(0), countPosn.get(1));
				  //  String [] split=extracted_date.split("-");
				   /* String NewFormat="Hello" + " "+split[1]+" "+split[0] +" "+split[2];
				    System.out.println("Changed question format is " + NewFormat);*/
				    entityList= new LinkedList<Entity>();
				    Entity e= new Entity();
				    e.setEntityType("DATE");
				    e.setEntityValue(extracted_date);
				    entityType="DATE";
				    entityKeyword=extracted_date;
				 }
				
			}
				 

			for (Entity entity : entityList) {
				if (entity.getEntityType().equals("PERSON")) {
					entityType = "0";
					entityKeyword = entity.getEntityValue();
					break;

				} else if (entity.getEntityType().equals("ORGANIZATION")) {
					entityType = "2";
					entityKeyword = entity.getEntityValue();
					break;
				}

				else if (entity.getEntityType().equals("LOCATION")) {
					entityType = "1";
					entityKeyword = entity.getEntityValue();
					break;
				} else if (entity.getEntityType().equals("DATE")
						|| entity.getEntityType().equals("MONEY")
						|| entity.getEntityType().equals("PERCENT")
						|| entity.getEntityType().equals("TIME")) {
					entityType = entity.getEntityType();
					entityKeyword = entity.getEntityValue();
					break;
				}
			}

			// System.out.println(entityType);
			// For generic queries which do not have a named entity finding
			// index type and keyword
	/*		if (entityType == null) {

				TreeMap<String, String> organizationMap = initOrganizationMap();
				TreeMap<String, String> placeMap = initPlaceMap();
				TreeMap<String, String> personMap = initPersonMap();

				
				 * List<String>posNounList=new ArrayList<String>(); for()
				 

				for (POS pos : posList) {
					if (pos.getPosType().contains("NN")) {

						if (personMap.containsKey(pos.getPosValue())) {
							entityType = "0";
							entityKeyword = pos.getPosValue();
							break;
						}

						else if (organizationMap.containsKey(pos.getPosValue())) {
							entityType = "2";
							entityKeyword = pos.getPosValue();
							break;
						}

						else if (placeMap.containsKey(pos.getPosValue())) {
							entityType = "1";
							entityKeyword = pos.getPosValue();
							break;
						}
					}
				}
			}*/

			/*
			 * for(POS pos:posList) { System.out.println(pos.getPosType());
			 * System.out.println(pos.getPosValue()); }
			 */
			// Determining question Type

			for (POS pos : posList) {
				if (pos.getPosType().contains("WDT")
						|| pos.getPosType().contains("WP")
						|| pos.getPosType().contains("WRB")) {
					questionType = pos.getPosValue();
				}

				// Same loop determining adjectives

				if (pos.getPosType().contains("JJ")) {
					adjective = pos.getPosValue() + " " + adjective;
					adjectiveType = pos.getPosType();

				}

				// Same loop determining verb

				List<String> verbForms = new ArrayList<String>();
				verbForms.add("has");
				verbForms.add("is");
				verbForms.add("had");
				verbForms.add("was");
				verbForms.add("did");
				verbForms.add("had been");
				verbForms.add("are");
				verbForms.add("have");
				verbForms.add("does");
				// verbForms.add("many");
				if (pos.getPosType().contains("VB")/*
													 * &&
													 * (!pos.getPosType().equals
													 * ("VBD"))
													 */) {
					if (verbForms.contains(pos.getPosValue()))
						continue;
					verb = pos.getPosValue() + " " + verb;
					verbType = pos.getPosType();
					// entityKeyword=entityKeyword+" "+verb;
				}

				if (pos.getPosType().equals("NNP")
						|| pos.getPosType().equals("NNPS")) {

					proper_Nouns = proper_Nouns + " " + pos.getPosValue();

					// verbType=pos.getPosType();
					// entityKeyword=entityKeyword+" "+verb;
				}
				if (pos.getPosType().equals("NN")
						|| pos.getPosType().equals("NNS")) {
					// Removing entity Keyword from any noun eg remove Nehru
					if (entityKeyword != null)
					{
						System.out.println("Entity keyword at this point" +entityKeyword);
						System.out.println("Matched POS at this  at this point" +pos.getPosValue());
					}
					if (entityKeyword != null
							&& (entityKeyword.contains(pos.getPosValue())|| entityKeyword.contains(pos.getPosValue().toUpperCase()))){
						
						
						continue;}
					noun =noun + " "+pos.getPosValue();
					// verbType=pos.getPosType();
					// entityKeyword=entityKeyword+" "+verb;
				}

				if (pos.getPosType().contains("CD")
						|| pos.getPosType().contains("JJ")
						&& entityType != null
						&& entityType.equalsIgnoreCase("DATE")) {
					if (entityKeyword != null
							&& entityKeyword.contains(pos.getPosValue()))
						continue;
					entityKeyword = pos.getPosValue() + " " + entityKeyword;
					// System.out.println("Date concat is:" +entityKeyword);
					// entityKeyword=entityKeyword+" "+verb;
				}
				if (pos.getPosType().contains("CD") && entityType == null) {
					
					entityType="NUMBER";
					entityKeyword = pos.getPosValue();
					System.out.println("Number entity keyword:" +entityKeyword);
					
				}
				
			}

			System.out.println("The entity type is:" + entityType);
			System.out.println("The entity Keyword is:" + entityKeyword);

			// Question type When,Entity Person We are searching some time event
			// related to person

			if (questionType == null) {
				System.out.println("Please enter a question");
			}

			if (questionType != null
					&& (entityType == null || entityType.equalsIgnoreCase("1")
							|| entityType.equalsIgnoreCase("0") || entityType
								.equalsIgnoreCase("2"))) {
				try {

					// System.out.println("We are searching some time event WHEN,Search with  keyWord");
					/* System.out.println(entityKeyword); */

					String residualKeyword = /*adjective + */noun + verb;
					if(residualKeyword.equals("") && adjective!=null)
					{
						 residualKeyword =adjective;
					}
					System.out.println("The residual keyword for search is :"
							+ residualKeyword);
					 Map<String, Map<String,String>> resMap= searcher.QuerySearcher(entityType, entityKeyword,
							questionType, residualKeyword, relWords,
							proper_Nouns);
					 System.out.println("Your answer is 10 9 8 7 6 5.........");
					 System.out.println(resMap);
					 return resMap;
				} catch (SolrServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Who died on 20 March 1727
			if (entityType != null
					&& questionType != null
					&& (entityType.equalsIgnoreCase("DATE")
							|| entityType.equalsIgnoreCase("NUMBER")
							|| entityType.equalsIgnoreCase("MONEY") || entityType
								.equalsIgnoreCase("PERCENT"))) {
				String residualKeyword = noun + verb;
				System.out.println("Question type" + questionType);
				if (questionType.equalsIgnoreCase("when"))

					residualKeyword = residualKeyword + "1";

				else if (questionType.equalsIgnoreCase("where"))

					residualKeyword = residualKeyword + "2";

				else if (questionType.equalsIgnoreCase("how"))

					residualKeyword = residualKeyword + "3";

				else if (questionType.equalsIgnoreCase("who"))

					residualKeyword = residualKeyword + "4";

				System.out.println("Residual keyword is:" + residualKeyword);
				try {
					// searcher.initializeFieldNameCollection();
					Map<String, Map<String,String>> resMap = searcher.reverseQuerySearcher(entityType, entityKeyword,
							questionType, residualKeyword,relWords);
					return resMap;
				} catch (SolrServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			/*
			 * if(entityType==null && questionType.equalsIgnoreCase("Who")) {
			 * String residualKeyword =noun+verb;
			 * System.out.println("Residual keyword is:"+residualKeyword); try {
			 * searcher.initializeFieldNameCollection();
			 * searcher.reverseQuerySearcher
			 * (entityType,residualKeyword,questionType,residualKeyword); }
			 * catch (SolrServerException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }
			 * 
			 * }
			 */

			/*
			 * if(entityType==null){
			 * 
			 * try { searcher.initializeFieldNameCollection();
			 * System.out.println
			 * ("We are searching some time event WHEN,Search with  keyWord");
			 * System.out.println(entityKeyword);
			 * 
			 * String residualKeyword =verb+noun+adjective;
			 * System.out.println(residualKeyword);
			 * //searcher.QuerySearcher(entityType
			 * ,entityKeyword,questionType,residualKeyword); } catch
			 * (SolrServerException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } }
			 */

			/*
			 * if(entityType!=null&&questionType!=null&&
			 * questionType.equalsIgnoreCase("Who")){
			 * System.out.println("We are searching name of person"); String
			 * newKeyword =verb+noun+adjective; try {
			 * searcher.QuerySearcher(entityType,entityKeyword,newKeyword); }
			 * catch (SolrServerException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }
			 * 
			 * }
			 */

			// Determine keywords

			/*
			 * for(POS pos:posList) { System.out.print(pos.getPosValue() );
			 * System.out.println(pos.getPosType());
			 * 
			 * }
			 */

			// System.out.println("The entity type is:"+entityType);
			// System.out.println("The entity Keyword is:"+entityKeyword);
			/*
			 * System.out.println(entityKeyword);
			 * System.out.println(questionType); System.out.println(adjective);
			 */

			/*
			 * for(POS pos:posList) { System.out.println(pos.getPosType());
			 * System.out.println(pos.getPosValue()); }
			 */
			/*
			 * if(questionDeterminer.contains("WP")||questionDeterminer.contains(
			 * "WRB")||questionDeterminer.contains("WDT") ) {
			 * System.out.println("Contains"); }
			 */
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private static TreeMap<String, String> initOrganizationMap() {
		TreeMap<String, String> organizationMap = new TreeMap<String, String>();
		organizationMap.put("press agencies", "");
		organizationMap.put("press agency", "");
		organizationMap.put("studios", "");
		organizationMap.put("studio", "");
		organizationMap.put("banks", "");
		organizationMap.put("bank", "");
		organizationMap.put("stock market", "");
		organizationMap.put("stock markets", "");
		organizationMap.put("manufacturers", "");
		organizationMap.put("manufacturer", "");
		organizationMap.put("cooperatives", "");
		organizationMap.put("newsrooms", "");
		organizationMap.put("newsroom", "");
		organizationMap.put("political party", "");
		organizationMap.put("political parties", "");
		organizationMap.put("terrorist organisations", "");
		organizationMap.put("terrorist organisation", "");
		organizationMap.put("ministry", "");
		organizationMap.put("ministries", "");
		organizationMap.put("newspaper", "");
		organizationMap.put("newspapers", "");
		organizationMap.put("council", "");
		organizationMap.put("councils", "");
		organizationMap.put("courts", "");
		organizationMap.put("court", "");
		organizationMap.put("political unions of countries", "");
		organizationMap.put("magazine", "");
		organizationMap.put("magazines", "");
		organizationMap.put("journal", "");
		organizationMap.put("journals", "");
		organizationMap.put("band", "");
		organizationMap.put("bands", "");
		organizationMap.put("choir", "");
		organizationMap.put("choirs", "");
		organizationMap.put("opera ", "");
		organizationMap.put("operas ", "");
		organizationMap.put("orchestra", "");
		organizationMap.put("orchestras", "");
		organizationMap.put("school", "");
		organizationMap.put("schools", "");
		organizationMap.put("university", "");
		organizationMap.put("universities", "");
		organizationMap.put("charity", "");
		organizationMap.put("charities", "");
		organizationMap.put("sport clubs", "");
		organizationMap.put("sport club", "");
		organizationMap.put("team", "");
		organizationMap.put("teams", "");
		organizationMap.put("sports team", "");
		organizationMap.put("sports teams", "");
		organizationMap.put("associations", "");
		organizationMap.put("association", "");
		organizationMap.put("theater", "");
		organizationMap.put("theaters", "");
		organizationMap.put("company", "");
		organizationMap.put("companies", "");
		organizationMap.put("theater companies", "");
		organizationMap.put("theater company", "");
		organizationMap.put("religious orders", "");
		organizationMap.put("religious order", "");
		organizationMap.put("youth organiation", "");
		organizationMap.put("youth organizations", "");
		return organizationMap;
	}

	private static TreeMap<String, String> initPlaceMap() {
		TreeMap<String, String> placeMap = new TreeMap<String, String>();
		placeMap.put("street", "");
		placeMap.put("motorway", "");
		placeMap.put("region", "");
		placeMap.put("village", "");
		placeMap.put("town", "");
		placeMap.put("towns", "");
		placeMap.put("cities", "");
		placeMap.put("city", "");
		placeMap.put("provinces", "");
		placeMap.put("province", "");
		placeMap.put("country", "");
		placeMap.put("countries", "");
		placeMap.put("continent", "");
		placeMap.put("continents", "");
		placeMap.put("bridge", "");
		placeMap.put("both", "");
		placeMap.put("port", "");
		placeMap.put("ports", "");
		placeMap.put("dam", "");
		placeMap.put("dams", "");
		placeMap.put("mountains", "");
		placeMap.put("mountain range", "");
		placeMap.put("mountain ranges", "");
		placeMap.put("wood", "");
		placeMap.put("woods", "");
		placeMap.put("river", "");
		placeMap.put("rivers", "");
		placeMap.put("wells", "");
		placeMap.put("field", "");
		placeMap.put("fields", "");
		placeMap.put("valley", "");
		placeMap.put("valleys", "");
		placeMap.put("garden", "");
		placeMap.put("gardens", "");
		placeMap.put("nature reserve", "");
		placeMap.put("nature reserves", "");
		placeMap.put("allotment", "");
		placeMap.put("allotments", "");
		placeMap.put("beach", "");
		placeMap.put("beaches", "");
		placeMap.put("national park", "");
		placeMap.put("national parks", "");
		placeMap.put("square", "");
		placeMap.put("opera house", "");
		placeMap.put("museum", "");
		placeMap.put("museums", "");
		placeMap.put("school", "");
		placeMap.put("schools", "");
		placeMap.put("market", "");
		placeMap.put("markets", "");
		placeMap.put("airport", "");
		placeMap.put("airports", "");
		placeMap.put("station", "");
		placeMap.put("stations", "");
		placeMap.put("swimming pool", "");
		placeMap.put("sports facilities", "");
		placeMap.put("hospital", "");
		placeMap.put("hospitals", "");
		placeMap.put("youth center", "");
		placeMap.put("park", "");
		placeMap.put("parks", "");
		placeMap.put("town hall", "");
		placeMap.put("town halls", "");
		placeMap.put("theater", "");
		placeMap.put("theaters", "");
		placeMap.put("cinema", "");
		placeMap.put("cinemas", "");
		placeMap.put("gallery", "");
		placeMap.put("galleries", "");
		placeMap.put("club houses", "");
		placeMap.put("university", "");
		placeMap.put("universities", "");
		placeMap.put("library", "");
		placeMap.put("libraries", "");
		placeMap.put("church", "");
		placeMap.put("churches", "");
		placeMap.put("medical center", "");
		placeMap.put("medical centers", "");
		placeMap.put("parking lot", "");
		placeMap.put("parking lots", "");
		placeMap.put("playground", "");
		placeMap.put("playgrounds", "");
		placeMap.put("cemetery", "");
		placeMap.put("cemeteries", "");
		placeMap.put("chemist", "");
		placeMap.put("chemists", "");
		placeMap.put("pub", "");
		placeMap.put("pubs", "");
		placeMap.put("restaurant", "");
		placeMap.put("restaurants", "");
		placeMap.put("depots", "");
		placeMap.put("hostels", "");
		placeMap.put("hotel", "");
		placeMap.put("industrial park", "");
		placeMap.put("industrial parks", "");
		placeMap.put("nightclub", "");
		placeMap.put("nightclubs", "");
		placeMap.put("music venue", "");
		placeMap.put("music venues", "");
		placeMap.put("house", "");
		placeMap.put("houses", "");
		placeMap.put("monastery", "");
		placeMap.put("monasteries", "");
		placeMap.put("creches", "");
		placeMap.put("mill", "");
		placeMap.put("mills", "");
		placeMap.put("army barrack", "");
		placeMap.put("army barracks", "");
		placeMap.put("castle", "");
		placeMap.put("castles", "");
		placeMap.put("retirement", "");
		placeMap.put("retirements", "");
		placeMap.put("home", "");
		placeMap.put("homes", "");
		placeMap.put("tower", "");
		placeMap.put("towers", "");
		placeMap.put("hall", "");
		placeMap.put("halls", "");
		placeMap.put("room", "");
		placeMap.put("rooms", "");
		placeMap.put("vicarages", "");
		placeMap.put("courtyard", "");
		placeMap.put("courtyards", "");
		placeMap.put("place", "");
		placeMap.put("places", "");
		placeMap.put("world", "");
		return placeMap;
	}

	private static TreeMap<String, String> initPersonMap() {

		TreeMap<String, String> personMap = new TreeMap<String, String>();
		personMap.put("scientists", "");
		personMap.put("scientist", "");
		personMap.put("footballer", "");
		personMap.put("footballers", "");
		personMap.put("cricketer", "");
		personMap.put("cricketers", "");
		personMap.put("players", "");
		personMap.put("player", "");
		personMap.put("artists", "");
		personMap.put("artist", "");
		personMap.put("writer", "");
		personMap.put("writers", "");
		personMap.put("economists", "");
		personMap.put("economist", "");
		personMap.put("musician", "");
		personMap.put("musicians", "");
		personMap.put("entrepreneur", "");
		personMap.put("office holder", "");
		personMap.put("office holders", "");
		personMap.put("criminal", "");
		personMap.put("criminals", "");
		personMap.put("sister-in-law", "");
		personMap.put("brother-in-law", "");
		personMap.put("spouse", "");
		personMap.put("children", "");
		personMap.put("child", "");
		personMap.put("mother", "");
		personMap.put("father", "");
		personMap.put("spouses", "");
		personMap.put("wife", "");
		personMap.put("wives", "");
		personMap.put("sisters-in-law", "");
		personMap.put("brothers-in-law", "");
		personMap.put("uncle", "");
		personMap.put("aunt", "");
		personMap.put("sister", "");
		personMap.put("brother", "");
		return personMap;
	}
}