package com.ir.qa.QueryProcessor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class MoreLikeThis {

	public SolrDocumentList moreLikeThisSuggestion(String id,
			String infobox_type) {

		HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr");
		SolrQuery queryParams = new SolrQuery();
		// queryParams.setQueryType("/" + MoreLikeThisParams.MLT);
		queryParams.setQueryType("/mlt");
		String queryString = "id:" + id;
		queryParams.setQuery(queryString);
		// queryParams.set("fl", "id,score");
		// queryParams.set("mlt.match.offset", "0");
		queryParams.set("mlt.mindf", "1");
		queryParams.set("mlt.mintf", "1");
		queryParams.setRows(20);
		if (infobox_type.contains("0")) {
			queryParams.set("mlt.fl", "work");
			queryParams.setFields("entity_name", "occupation", "infobox_type","wikidoc_title");

		}
		if (infobox_type.contains("1")) {
			queryParams.set("mlt.fl", "mlt_place");
			queryParams.setFields("id", "entity_name", "subdivision_name",
					"government_type", "time_zone","wikidoc_title");
		}
		if (infobox_type.contains("2")) {

			queryParams.set("mlt.fl", "location");
			queryParams.setFields("entity_name", "industry", "location",
					"type", "headquarters", "area_served","wikidoc_title");
		}
		/*
		 * queryParams.set("mlt.fl","occupation","subdivision_type","governing_body"
		 * ,"occupation","infobox_type","spouse");
		 * queryParams.setFields("entity_name","occupation","infobox_type");
		 */
		// System.out.println(queryParams);
		QueryResponse response = null;
		try {
			response = solr.query(queryParams);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SolrDocumentList results = (SolrDocumentList) response.getResponse()
				.get("response");
		SolrDocumentList resultWithEntityNames = new SolrDocumentList();
		for (SolrDocument result : results) {

			if (result.getFieldValue("entity_name") == null
					|| result.getFieldValue("entity_name").toString()
							.equals("[]"))
				continue;
			else
				resultWithEntityNames.add(result);
		}

		/*
		 * SolrDocumentList results = (SolrDocumentList) response.getResponse();
		 */

		/*
		 * SolrDocumentList results = (SolrDocumentList)
		 * response.getResponse().get("match");
		 */
		/* System.out.println("Total number of matching docs:" +results.size()); */
		// System.out.println(results);
		/*
		 * System.out.println("results Size: " + results.size() + " is not: " +
		 * 1, results.size() == 1);
		 */
		return resultWithEntityNames;
	}
}
