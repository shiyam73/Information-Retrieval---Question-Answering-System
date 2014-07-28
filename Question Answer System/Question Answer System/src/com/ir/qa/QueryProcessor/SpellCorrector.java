package com.ir.qa.QueryProcessor;
/*
 * Copyright 2008-2011 Grant Ingersoll, Thomas Morton and Drew Farris
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * -------------------
 * To purchase or learn more about Taming Text, by Grant Ingersoll, Thomas Morton and Drew Farris, visit
 * http://www.manning.com/ingersoll
 */



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

//<start id="did-you-mean.corrector"/>
public class SpellCorrector {

 
  
  private float threshold;
  
  public SpellCorrector(float threshold) 
    throws MalformedURLException { 
	  this.threshold = threshold;
  }
  
  public String topSuggestion(String spelling)
          throws SolrServerException {
	  HttpSolrServer solr = new HttpSolrServer("http://localhost:8983/solr");
	  SolrQuery query = new SolrQuery();
	    query.setFields("name");
	    query.setRows(50);
	     query.setQuery("wordNGram:"+spelling); //<co id="co.dym.field"/>
	     QueryResponse response = solr.query(query);
	     SolrDocumentList results = response.getResults();
	     Levenshtein levenshtein=new Levenshtein();
    float maxDistance = 0;
    float distance=0;
    String suggestion = null;
    List<String> nameList= new ArrayList<String>();
    
    for (int i = 0; i < results.size();i++ ) {
      // System.out.println(results.get(i));
         SolrDocument doc = results.get(i);
         
         nameList = (List<String>) doc.getFieldValue("name");
        for(String name_suggestions:nameList)
        {
        	 distance = levenshtein.getSimilarity(name_suggestions, spelling);
             System.out.println(name_suggestions);
             
             System.out.println(distance);

        	 if (distance > maxDistance) {
                 maxDistance = distance;
                 suggestion = name_suggestions; 
               }
        }	
      
       
        if (maxDistance > threshold) { //<co id="co.dym.threshold"/>
           
        	return suggestion;
          }
    }

   
    return null;
  }  
}
/*
<calloutlist>
<callout arearefs="co.dym.num"><para>The number of n-gram matches to consider.</para></callout>
<callout arearefs="co.dym.field"><para>Query the field which contains the n-gram.</para></callout>
<callout arearefs="co.dym.edit"><para>Compute the edit distance.</para></callout>
<callout arearefs="co.dym.max"><para>Keep best suggestion.</para></callout>
<callout arearefs="co.dym.threshold"><para>Check threshold otherwise return no suggestion.</para></callout>
</calloutlist>
 */
//<end id="did-you-mean.corrector"/>
