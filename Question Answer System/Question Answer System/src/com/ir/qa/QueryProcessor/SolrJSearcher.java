package com.ir.qa.QueryProcessor;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.commons.lang.StringUtils;

//import edu.stanford.nlp.util.StringUtils;

import com.ir.qa.QueryProcessor.QueryProcessorUtil.Field;

import java.net.MalformedURLException;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class SolrJSearcher {

	Collection<String> fieldNames = new ArrayList<String>();
	Collection<String> FieldNames_for_WHEN = new ArrayList<String>();
	Collection<String> FieldNames_for_WHO = new ArrayList<String>();
	Collection<String> FieldNames_for_WHAT = new ArrayList<String>();
	Collection<String> FieldNames_for_WHICH = new ArrayList<String>();
	Collection<String> FieldNames_for_HOW = new ArrayList<String>();
	Collection<String> FieldNames_for_WHERE = new ArrayList<String>();
	Collection<String> fieldsNotCountable = new HashSet<String>();
	HttpSolrServer solr = null;
	SolrQuery query = new SolrQuery();
	Levenshtein levenshtein = new Levenshtein();
	String queryString;

	public Collection<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(Collection<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public Map<String, Map<String, String>> QuerySearcher(String entityType,
			String entityKeyword, String questionType, String fieldToMatch,
			Map<String, String> relWords, String entityNoun)
			throws MalformedURLException, SolrServerException {

		System.out.println("Question type" + questionType);
		if (questionType.equalsIgnoreCase("when"))

			fieldToMatch = fieldToMatch.trim() + "1";

		else if (questionType.equalsIgnoreCase("where"))

			fieldToMatch = fieldToMatch.trim() + "2";

		else if (questionType.equalsIgnoreCase("how"))

			fieldToMatch = fieldToMatch.trim() + "3";

		else if (questionType.equalsIgnoreCase("who"))

			fieldToMatch = fieldToMatch.trim() + "4";

		System.out.println("The new field to match is:" + fieldToMatch);
		HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr");

		// IF ENTITY TYPE IS PRESENT,WE USE IT TO CATEGORIZE
		// BOTH CATEGORY TYPES AND CATEGORY FILTER DEPEND ON ENTITY
		// When was Issac Newton born

		Collection<String> fieldNamesToCheck;
		String bestMatchedField = "";

		if (questionType.equalsIgnoreCase("when"))
			fieldNamesToCheck = FieldNames_for_WHEN;
		else if (questionType.equalsIgnoreCase("who"))
			fieldNamesToCheck = FieldNames_for_WHO;
		else if (questionType.equalsIgnoreCase("what"))
			fieldNamesToCheck = FieldNames_for_WHAT;
		else if (questionType.equalsIgnoreCase("which"))
			fieldNamesToCheck = FieldNames_for_WHICH;
		else if (questionType.equalsIgnoreCase("how"))
			fieldNamesToCheck = FieldNames_for_HOW;
		else if (questionType.equalsIgnoreCase("where"))
			fieldNamesToCheck = FieldNames_for_WHICH;
		else
			fieldNamesToCheck = null;

		ArrayList<String> fieldList = (ArrayList<String>) QueryProcessorUtil
				.closestFields(levenshtein, fieldToMatch, fieldNamesToCheck,
						relWords);

		SolrDocumentList results = null;

		for (int i = 0; i < 6; i++) {
			SolrQuery sq = SolrQueryGenerator.generateQuery(entityType,
					entityKeyword, entityNoun, fieldList, i);
			System.out.println("SolrQuery::" + sq);
			QueryResponse response = solr.query(sq);
			results = response.getResults();
			System.out.println("Results::" + results);
			if (results != null && !results.isEmpty()) {
				break;
			}
		}

		String suggestion = "";

		if (results == null || results.isEmpty()) {

			SpellCorrector spellCorrector = new SpellCorrector((float) 0.3);
			System.out.println("Proper noun for correction is:" + entityNoun);
			suggestion = spellCorrector.topSuggestion(entityNoun);
			System.out.println("Did you mean::" + suggestion);
			for (int i = 0; i < 6; i++) {
				SolrQuery sq = SolrQueryGenerator.generateQuery(entityType,
						suggestion, suggestion, fieldList, i);
				System.out.println("SolrQuery::" + sq);
				QueryResponse response = solr.query(sq);
				results = response.getResults();
				System.out.println("Results::" + results);
				if (results != null && !results.isEmpty()) {
					break;
				}
			}
		}

		// System.out.println("BestMatchedField::" + bestMatchedField);
		// Map<String,String> feild=new HashMap<String,String>();
		Map<String, Map<String, String>> res = new HashMap<String, Map<String, String>>();
		Integer count = 0;

		if (results.size() == 0) {
			// System.out.println("Searched everything,nothing found");
			return null;
		} else {
			System.out.println(results.get(0).getFieldValue("infobox_type")
					.toString());

			// START
			for (int j = 0; j < fieldList.size(); j++) {
				System.out.println("entitykey::" + entityKeyword);
				System.out.println("Field list::" + fieldList.get(j));
				int fieldInDocFrequency = 0;
				for (int i = 0; i < results.size(); i++) {
					if (results.get(i).get(fieldList.get(j)) != null) {
						fieldInDocFrequency = fieldInDocFrequency + 1;
						if (questionType.equalsIgnoreCase("how")) {
							Map<String, String> feild = new HashMap<String, String>();
							if (!(fieldsNotCountable.contains(fieldList.get(j)))) {
								if (results.get(i)
										.getFieldValue(fieldList.get(j))
										.toString().length() != 0) {
									count = StringUtils.countMatches(
											results.get(i)
													.getFieldValue(
															fieldList.get(j))
													.toString(), ",");
									++count;
								}
								String entityName = results.get(i)
										.get("entity_name").toString()
										.replaceAll("\\[|\\]", "");
								if (entityName.indexOf(',') == -1) {
									feild.put("entity_name", entityName);
								} else {
									feild.put(
											"entity_name",
											entityName.substring(0,
													entityName.indexOf(',')));
								}
								String infoBoxType = results.get(i)
										.get("infobox_type").toString()
										.replaceAll("\\[|\\]", "");
								feild.put("infobox_type", infoBoxType);
								feild.put("id", results.get(i).get("id").toString().replaceAll("\\[|\\]", ""));
								feild.put("wikidoc_title",results.get(i).get("wikidoc_title").toString());
								feild.put("query_field", fieldList.get(j).toString() + "_count");
								feild.put("query_result", count.toString());
								res.put(i + "", feild);
							}else{
								System.out.println("");
								String entityName = results.get(i)
										.get("entity_name").toString()
										.replaceAll("\\[|\\]", "");
								if (entityName.indexOf(',') == -1) {
									feild.put("entity_name", entityName);
								} else {
									feild.put(
											"entity_name",
											entityName.substring(0,
													entityName.indexOf(',')));
								}
								String infoBoxType = results.get(i)
										.get("infobox_type").toString()
										.replaceAll("\\[|\\]", "");
								feild.put("infobox_type", infoBoxType);
								feild.put("id", results.get(i).get("id").toString()
										.replaceAll("\\[|\\]", ""));
								feild.put("query_field", fieldList.get(j)
										.toString());
								feild.put("wikidoc_title",
										results.get(i).get("wikidoc_title")
												.toString());
								feild.put(
										"query_result",
										results.get(i).get(fieldList.get(j))
												.toString()
												.replaceAll("\\[|\\]", ""));
								res.put(i + "", feild);
							}
							// System.out.println("How: "+res);
						} else {
							System.out.println("Hi");
							Map<String, String> feild = new HashMap<String, String>();
							System.out.println("");
							String entityName = results.get(i)
									.get("entity_name").toString()
									.replaceAll("\\[|\\]", "");
							if (entityName.indexOf(',') == -1) {
								feild.put("entity_name", entityName);
							} else {
								feild.put(
										"entity_name",
										entityName.substring(0,
												entityName.indexOf(',')));
							}
							String infoBoxType = results.get(i)
									.get("infobox_type").toString()
									.replaceAll("\\[|\\]", "");
							feild.put("infobox_type", infoBoxType);
							feild.put("id", results.get(i).get("id").toString()
									.replaceAll("\\[|\\]", ""));
							feild.put("query_field", fieldList.get(j)
									.toString());
							feild.put("wikidoc_title",
									results.get(i).get("wikidoc_title")
											.toString());
							feild.put(
									"query_result",
									results.get(i).get(fieldList.get(j))
											.toString()
											.replaceAll("\\[|\\]", ""));
							res.put(i + "", feild);
						}
					}
				}
				if (res.isEmpty()) {
					System.out
							.println("The result is stll empty:: Going for next field");
				} else {
					break;
				}
			}
		}
		return res;
		// return results.get(0).toString();
	}

	public static Map<String, Map<String, String>> moreLikeThisFunction(
			String id, String infobox_type) {
		MoreLikeThis moreLikeThis = new MoreLikeThis();
		Map<String, Map<String, String>> documentNames = new HashMap<String, Map<String, String>>();
		SolrDocumentList docList = moreLikeThis.moreLikeThisSuggestion(id,
				infobox_type);
		int i = 0;
		for (SolrDocument doc : docList) {
			String entityName = doc.getFieldValue("entity_name").toString()
					.replaceAll("\\]|\\[", "");
			Map<String, String> fields = new HashMap<String, String>();
			if (entityName.indexOf(',') == -1) {
				fields.put("entity_name", entityName);
			} else {
				fields.put("entity_name",
						entityName.substring(0, entityName.indexOf(',')));
			}
			fields.put("wikidoc_title", doc.getFieldValue("wikidoc_title")
					.toString());
			documentNames.put(i + "", fields);
			i++;
		}
		return documentNames;
	}

	// For Query types
	// What was formed on July 29,1958 (55 years ago)
	// Who was born on 20 March 1727
	// Who died on 20 March 1727

	public Map<String, Map<String, String>> reverseQuerySearcher(
			String entityType, String entityKeyword, String questionType,
			String fieldToMatch, Map<String, String> relWords)
			throws SolrServerException {
		HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr");
		SolrQuery query = new SolrQuery();

		Collection<String> fieldNamesToCheck;
		float distance = 0;
		float max = 0;
		String bestMatchedField = "";
		Levenshtein levenshtein = new Levenshtein();

		fieldNamesToCheck = fieldNames;

		ArrayList<String> fieldList = (ArrayList<String>) QueryProcessorUtil
				.closestFields(levenshtein, fieldToMatch, fieldNamesToCheck,
						relWords);

		Map<String, Map<String, String>> res = new HashMap<String, Map<String, String>>();

		/*
		 * for (String fieldName : fieldNamesToCheck) { distance =
		 * levenshtein.getSimilarity(fieldToMatch, fieldName); if (distance >
		 * max) { max = distance; bestMatchedField = fieldName; } }
		 */

		System.out.println("The best matched field using levenshtein is :"
				+ fieldList.get(0));

		// String categoryString = bestMatchedField + ":" + entityKeyword;
		String categoryString = fieldList.get(0) + ":" + "\"" + entityKeyword
				+ "\"";
		System.out.println(categoryString);
		// query.addFilterQuery(categoryString);
		queryString = entityKeyword;
		/*
		 * if (entityType == null) { queryString =
		 * entityKeyword.replaceFirst(bestMatchedField, "");
		 * System.out.println("replaced String is:" + queryString); }
		 */
		query.setQuery(categoryString);
		query.setStart(0);

		query.set("defType", "edismax");

		query.addField("entity_name");
		query.addField("infobox_type");
		query.addField("wikidoc_title");
		query.addField("id");
		query.addField(fieldList.get(0));
		QueryResponse response = solr.query(query);
		SolrDocumentList results = response.getResults();

		if (results.size() == 0) {
			System.out.println("Searched everything,nothing found");
			return null;
		} else {

			/*
			 * System.out.println(results.get(0).getFieldValue("infobox_type").
			 * toString()); Map<String, Map<String, String>>
			 * moreLikeThisDocs=moreLikeThisFunction
			 * (results.get(0).getFieldValue
			 * ("id").toString(),results.get(0).getFieldValue
			 * ("infobox_type").toString());
			 */

			for (int i = 0; i < results.size(); i++) {
				System.out.println(results.get(i));
				if ((results.get(i).get("entity_name") != null && !results
						.get(i).getFieldValue("entity_name").toString()
						.equals("[]"))) {
					Map<String, String> feild = new HashMap<String, String>();
					String infoBoxType = results.get(i).get("infobox_type")
							.toString().replaceAll("\\[|\\]", "");
					String entityName = results.get(i).get("entity_name")
							.toString().replaceAll("\\[|\\]", "");

					if (entityName.indexOf(',') == -1) {
						feild.put("entity_name", entityName);
					} else {
						feild.put("entity_name", entityName.substring(0,
								entityName.indexOf(',')));
					}
					feild.put("infobox_type", infoBoxType);
					feild.put("id", results.get(i).get("id").toString()
							.replaceAll("\\[|\\]", ""));
					feild.put("query_field", fieldList.get(0).toString());
					feild.put("wikidoc_title",
							results.get(i).get("wikidoc_title").toString());
					feild.put("query_result",
							results.get(i).get(fieldList.get(0)).toString()
									.replaceAll("\\[|\\]", ""));
					res.put(i + "", feild);

					// feild.put(fieldList.get(0).toString(),results.get(i).get(fieldList.get(0)).toString()
					// );
				}
			}

			System.out.println(res);
			/*
			 * for (int i = 0; i < results.size(); i++) {
			 * System.out.println(results.get(i));
			 * 
			 * }
			 */
			// Logic to score in case of multiple documents returned
			return res;
		}
		// return results.get(0).toString();
	}

	public void initializeFieldNameCollection(HashSet<String> fieldTypes) {
		try {

			System.out.println(fieldNames);
			FieldNames_for_WHO.add("name");

			// FieldNames_for_WHAT.add("honorific_prefix");

			// FieldNames_for_WHAT.add("honorific_suffix");

			// FieldNames_for_WHAT.add("birth_name");

			FieldNames_for_WHEN.add("baptism_date");
			FieldNames_for_WHEN.add("birth_date");
			FieldNames_for_WHERE.add("birth_place");
			FieldNames_for_WHEN.add("disappeared_date");
			FieldNames_for_WHERE.add("disappeared_place");
			// FieldNames_for_WHAT.add("disappeared_status");
			FieldNames_for_WHEN.add("death_date");
			FieldNames_for_WHERE.add("death_place");
			FieldNames_for_HOW.add("death_cause");
			FieldNames_for_WHERE.add("body_discovered");
			FieldNames_for_WHERE.add("resting_place");
			FieldNames_for_HOW.add("monuments");
			FieldNames_for_WHERE.add("residence");
			FieldNames_for_HOW.add("other_names");
			FieldNames_for_WHERE.add("alma_mater");
			FieldNames_for_HOW.add("years_active");
			FieldNames_for_WHEN.add("years_active");
			FieldNames_for_WHO.add("employer");
			FieldNames_for_WHO.add("agent");
			FieldNames_for_HOW.add("notable_works");
			FieldNames_for_WHERE.add("home_town");
			FieldNames_for_HOW.add("salary");
			FieldNames_for_HOW.add("net_worth");
			FieldNames_for_HOW.add("height");
			FieldNames_for_HOW.add("weight");
			FieldNames_for_WHO.add("opponents");
			FieldNames_for_HOW.add("party");
			FieldNames_for_WHO.add("spouse");
			FieldNames_for_WHO.add("partner");
			FieldNames_for_HOW.add("partner");
			FieldNames_for_HOW.add("children");
			FieldNames_for_WHO.add("parents");
			FieldNames_for_WHO.add("relatives");
			FieldNames_for_HOW.add("awards");
			FieldNames_for_WHO.add("author");
			FieldNames_for_WHEN.add("test_debut_date");
			FieldNames_for_WHEN.add("test_debut_year");
			FieldNames_for_WHO.add("test_debut_against");
			FieldNames_for_WHEN.add("odi_debut_date");
			FieldNames_for_WHEN.add("odi_debut_year");
			FieldNames_for_WHO.add("odi_debut_against");
			FieldNames_for_HOW.add("carreer_prize_money");
			FieldNames_for_WHEN.add("turned_pro");
			FieldNames_for_WHERE.add("office");
			FieldNames_for_WHEN.add("term_start");
			FieldNames_for_WHEN.add("term_end");
			FieldNames_for_WHO.add("predecessor");
			FieldNames_for_WHO.add("successor");
			FieldNames_for_WHEN.add("founded");
			FieldNames_for_WHO.add("founder");
			FieldNames_for_WHEN.add("extinction");
			FieldNames_for_WHO.add("merger");
			FieldNames_for_WHEN.add("merged");
			FieldNames_for_WHERE.add("headquaters");
			FieldNames_for_WHERE.add("location");
			FieldNames_for_WHERE.add("region_served");
			FieldNames_for_WHERE.add("area_served");
			FieldNames_for_WHO.add("key_people");
			FieldNames_for_WHO.add("subsidiaries");
			FieldNames_for_HOW.add("subsidiaries");
			FieldNames_for_WHO.add("affiliations");
			// FieldNames_for_H.add("subsidiaries");
			FieldNames_for_WHO.add("parent_organization");
			FieldNames_for_HOW.add("budget");
			FieldNames_for_HOW.add("num_staff");
			FieldNames_for_HOW.add("num_volunteers");
			FieldNames_for_WHO.add("chief1_name");
			FieldNames_for_HOW.add("num_employees");
			FieldNames_for_WHERE.add("origin");
			// FieldNames_for_WHEN.add("established_date2");
			// FieldNames_for_WHEN.add("established_date1");
			FieldNames_for_WHERE.add("location_city");
			FieldNames_for_WHERE.add("location_country");
			FieldNames_for_WHO.add("founders");
			FieldNames_for_HOW.add("founders");
			FieldNames_for_HOW.add("products");
			FieldNames_for_HOW.add("revenue");
			FieldNames_for_HOW.add("operating_income");
			FieldNames_for_HOW.add("net_income");
			FieldNames_for_HOW.add("assets");
			FieldNames_for_HOW.add("equity");
			FieldNames_for_HOW.add("divisions");
			FieldNames_for_WHO.add("owner");
			FieldNames_for_WHO.add("chairman");
			FieldNames_for_WHO.add("manager");
			FieldNames_for_WHO.add("coach");
			FieldNames_for_WHO.add("head_coach");
			FieldNames_for_WHO.add("current_coach");
			FieldNames_for_WHO.add("current_captain");
			FieldNames_for_WHO.add("captain");
			FieldNames_for_HOW.add("ipl_wins");
			FieldNames_for_HOW.add("visitors");
			// FieldNames_for_.add("win.loss_record");
			FieldNames_for_HOW.add("clt20_wins");
			FieldNames_for_WHEN.add("test_status_year");
			FieldNames_for_WHEN.add("first_test_match");
			FieldNames_for_HOW.add("number_of_tests");
			FieldNames_for_WHO.add("leader");
			FieldNames_for_WHO.add("leader_name1");
			// FieldNames_for_WHEN.add("established_date3");
			// FieldNames_for_WHEN.add("established_date4");
			// FieldNames_for_WHEN.add("established_date5");
			FieldNames_for_HOW.add("percent_water");
			FieldNames_for_WHEN.add("population_census_year");
			FieldNames_for_HOW.add("basin_countries");
			FieldNames_for_HOW.add("left_tribs");
			FieldNames_for_HOW.add("right_tribs");
			FieldNames_for_WHO.add("discoverer");
			FieldNames_for_WHERE.add("discovery_site");
			FieldNames_for_WHEN.add("discovered");
			FieldNames_for_HOW.add("discovery_method");
			FieldNames_for_HOW.add("avg_speed");
			FieldNames_for_HOW.add("surface_grav");
			FieldNames_for_WHEN.add("established_date");
			FieldNames_for_WHEN.add("created");
			FieldNames_for_WHEN.add("date_opened");
			// FieldNames_for_WHY.add("operating_income");
			FieldNames_for_HOW.add("seat");
			FieldNames_for_WHO.add("governing_body");
			FieldNames_for_HOW.add("width");
			FieldNames_for_WHO.add("operator");
			FieldNames_for_WHEN.add("built");
			FieldNames_for_WHO.add("architect");
			FieldNames_for_HOW.add("num_animals");
			FieldNames_for_HOW.add("num_species");
			FieldNames_for_HOW.add("members");
			FieldNames_for_WHO.add("members");
			FieldNames_for_HOW.add("exhibits");
			FieldNames_for_HOW.add("collection");
			FieldNames_for_WHO.add("director");
			FieldNames_for_WHO.add("president");
			// FieldNames_for_HOW.add("public_transit");
			FieldNames_for_HOW.add("ethinic_groups");
			FieldNames_for_WHO.add("name");
			// FieldNames_for_WHAT.add("honorific_prefix");
			// FieldNames_for_WHAT.add("honorific_suffix");
			// FieldNames_for_WHAT.add("birth_name");
			FieldNames_for_WHEN.add("baptism_date");
			FieldNames_for_WHEN.add("birth_date");
			FieldNames_for_WHERE.add("birth_place");
			FieldNames_for_WHEN.add("disappeared_date");
			FieldNames_for_WHERE.add("disappeared_place");
			// FieldNames_for_WHAT.add("disappeared_status");
			FieldNames_for_WHEN.add("death_date");
			FieldNames_for_WHERE.add("death_place");
			FieldNames_for_HOW.add("death_cause");
			FieldNames_for_WHERE.add("body_discovered");
			FieldNames_for_WHERE.add("resting_place");
			FieldNames_for_HOW.add("monuments");
			FieldNames_for_WHERE.add("residence");
			FieldNames_for_HOW.add("other_names");
			FieldNames_for_WHERE.add("alma_mater");
			FieldNames_for_HOW.add("years_active");
			FieldNames_for_WHEN.add("years_active");
			FieldNames_for_WHO.add("employer");
			FieldNames_for_WHO.add("agent");
			FieldNames_for_HOW.add("notable_works");
			FieldNames_for_WHERE.add("home_town");
			FieldNames_for_HOW.add("salary");
			FieldNames_for_HOW.add("net_worth");
			FieldNames_for_HOW.add("height");
			FieldNames_for_HOW.add("weight");
			FieldNames_for_WHO.add("opponents");
			FieldNames_for_HOW.add("party");
			FieldNames_for_HOW.add("spouse");
			FieldNames_for_WHO.add("spouse");
			FieldNames_for_WHO.add("partner");
			FieldNames_for_HOW.add("partner");
			FieldNames_for_WHO.add("children");
			FieldNames_for_WHO.add("parents");
			FieldNames_for_WHO.add("relatives");
			FieldNames_for_HOW.add("awards");
			FieldNames_for_WHO.add("author");
			FieldNames_for_WHEN.add("test_debut_date");
			FieldNames_for_WHEN.add("test_debut_year");
			FieldNames_for_WHO.add("test_debut_against");
			FieldNames_for_WHEN.add("odi_debut_date");
			FieldNames_for_WHEN.add("odi_debut_year");
			FieldNames_for_WHO.add("odi_debut_against");
			FieldNames_for_HOW.add("carreer_prize_money");
			FieldNames_for_WHEN.add("turned_pro");
			FieldNames_for_WHERE.add("office");
			FieldNames_for_WHEN.add("term_start");
			FieldNames_for_WHEN.add("term_end");
			FieldNames_for_WHO.add("predecessor");
			FieldNames_for_WHO.add("successor");
			FieldNames_for_WHEN.add("founded");
			FieldNames_for_WHO.add("founder");
			FieldNames_for_WHEN.add("extinction");
			FieldNames_for_WHO.add("merger");
			FieldNames_for_WHEN.add("merged");
			FieldNames_for_WHERE.add("headquaters");
			FieldNames_for_WHERE.add("location");
			FieldNames_for_WHERE.add("region_served");
			FieldNames_for_WHERE.add("area_served");
			FieldNames_for_WHO.add("key_people");
			FieldNames_for_WHO.add("subsidiaries");
			FieldNames_for_HOW.add("subsidiaries");
			FieldNames_for_WHO.add("affiliations");
			// FieldNames_for_H.add("subsidiaries");
			FieldNames_for_WHO.add("parent_organization");
			FieldNames_for_HOW.add("budget");
			FieldNames_for_HOW.add("num_staff");
			FieldNames_for_HOW.add("num_volunteers");
			FieldNames_for_WHO.add("chief1_name");
			FieldNames_for_HOW.add("num_employees");
			FieldNames_for_WHERE.add("origin");
			// FieldNames_for_WHEN.add("established_date2");
			// FieldNames_for_WHEN.add("established_date1");
			FieldNames_for_WHERE.add("location_city");
			FieldNames_for_WHERE.add("location_country");
			FieldNames_for_WHO.add("founders");
			FieldNames_for_HOW.add("founders");
			FieldNames_for_HOW.add("products");
			FieldNames_for_HOW.add("revenue");
			FieldNames_for_HOW.add("operating_income");
			FieldNames_for_HOW.add("net_income");
			FieldNames_for_HOW.add("assets");
			FieldNames_for_HOW.add("equity");
			FieldNames_for_HOW.add("divisions");
			FieldNames_for_WHO.add("owner");
			FieldNames_for_WHO.add("chairman");
			FieldNames_for_WHO.add("manager");
			FieldNames_for_WHO.add("head_coach");
			FieldNames_for_WHO.add("current_coach");
			FieldNames_for_WHO.add("current_captain");
			FieldNames_for_WHO.add("captain");
			FieldNames_for_HOW.add("ipl_wins");
			FieldNames_for_HOW.add("visitors");
			// FieldNames_for_.add("win.loss_record");
			FieldNames_for_HOW.add("clt20_wins");
			FieldNames_for_WHEN.add("test_status_year");
			FieldNames_for_WHEN.add("first_test_match");
			FieldNames_for_WHEN.add("formation");
			FieldNames_for_HOW.add("number_of_tests");
			FieldNames_for_WHO.add("leader_name1");
			// FieldNames_for_WHEN.add("established_date3");
			// FieldNames_for_WHEN.add("established_date4");
			// FieldNames_for_WHEN.add("established_date5");
			FieldNames_for_HOW.add("percent_water");
			FieldNames_for_WHEN.add("population_census_year");
			FieldNames_for_HOW.add("basin_countries");
			FieldNames_for_HOW.add("left_tribs");
			FieldNames_for_HOW.add("right_tribs");
			FieldNames_for_WHO.add("discoverer");
			FieldNames_for_WHERE.add("discovery_site");
			FieldNames_for_WHEN.add("discovered");
			FieldNames_for_HOW.add("discovery_method");
			FieldNames_for_HOW.add("avg_speed");
			FieldNames_for_HOW.add("surface_grav");
			FieldNames_for_WHEN.add("established_date");
			FieldNames_for_WHEN.add("created");
			FieldNames_for_WHEN.add("date_opened");
			// FieldNames_for_WHY.add("operating_income");
			FieldNames_for_HOW.add("seat");
			FieldNames_for_WHO.add("governing_body");
			FieldNames_for_HOW.add("width");
			FieldNames_for_WHO.add("operator");
			FieldNames_for_WHEN.add("built");
			FieldNames_for_WHO.add("architect");
			FieldNames_for_HOW.add("num_animals");
			FieldNames_for_HOW.add("num_species");
			FieldNames_for_HOW.add("members");
			FieldNames_for_WHO.add("members");
			FieldNames_for_HOW.add("exhibits");
			FieldNames_for_HOW.add("collection");
			FieldNames_for_WHO.add("director");
			FieldNames_for_WHO.add("president");
			// FieldNames_for_HOW.add("public_transit");
			FieldNames_for_HOW.add("ethinic_groups");
			FieldNames_for_WHAT = fieldNames;
			// FieldNames_for_WHICH = fieldNames;
			FieldNames_for_WHICH = fieldNames;

			fieldsNotCountable.add("width");
			fieldsNotCountable.add("surface_grav");
			fieldsNotCountable.add("seat");
			fieldsNotCountable.add("avg_speed");
			fieldsNotCountable.add("discovery_method");
			fieldsNotCountable.add("percent_water");
			fieldsNotCountable.add("number_of_tests");
			fieldsNotCountable.add("visitors");
			fieldsNotCountable.add("clt20_wins");
			fieldsNotCountable.add("ipl_wins");
			fieldsNotCountable.add("divisions");
			fieldsNotCountable.add("equity");
			fieldsNotCountable.add("assets");
			fieldsNotCountable.add("net_income");
			fieldsNotCountable.add("operating_income");
			fieldsNotCountable.add("death_cause");
			fieldsNotCountable.add("revenue");
			fieldsNotCountable.add("budget");
			fieldsNotCountable.add("carreer_prize_money");
			fieldsNotCountable.add("weight");
			fieldsNotCountable.add("height");
			fieldsNotCountable.add("employees");
			fieldsNotCountable.add("death_cause");
			fieldsNotCountable.add("years_active");
			fieldsNotCountable.add("salary");
			fieldsNotCountable.add("net_worth");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}