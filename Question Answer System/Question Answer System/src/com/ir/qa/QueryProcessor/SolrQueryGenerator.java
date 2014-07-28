package com.ir.qa.QueryProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;


public class SolrQueryGenerator {

	private static Levenshtein levenshtein = new Levenshtein();
	public static SolrQuery generateQuery(String entityType,
			String entityKeyword, String entityNoun,
			 ArrayList<String> fieldList, int level) {

		SolrQuery query = new SolrQuery();
		String queryString = "";
		String queryField = "name";
		String categoryString = "";

		if (level % 2 == 0) {
			categoryString = "infobox_type:" + entityType;
		}
		
		if(level < 4){
			queryField = "entity_name";
		}else{
			queryField = "text";
		}

		String quotes = "\"";
		if(level == 3 || level == 4){
			quotes = "";
		}
		if (entityKeyword != null) {
			System.out.println("Querying on name field");
			queryString = queryField + ":" +quotes+"*" + entityKeyword.trim() + "*"+quotes;
		} else if (entityNoun != null) {
			System.out.println("entity noun");
			queryString = queryField + ":" + quotes +"*" + entityNoun.trim() + "*"+ quotes;
		}
		// queryString="name:"+"\"*"+entityKeyword+"*\"";
		System.out.println("queryString" + queryString);
		query = new SolrQuery();
		query.setQuery(queryString);

		query.addFilterQuery(categoryString);
		
		Iterator<String> fieldIt = fieldList.iterator();
		while(fieldIt.hasNext()){
			query.addField(fieldIt.next());
		}
		query.addField(queryField);
		query.addField("infobox_type");
		query.addField("wikidoc_title");
		query.addField("id");
		//query.addField("id");
		// query.setFields("name");
		query.setStart(0);
		query.set("defType", "edismax");
		
		System.out.println("Level::"+level+"QueryString::"+queryString);
		System.out.println("CategoryString::"+categoryString);
		return query;
	}
}