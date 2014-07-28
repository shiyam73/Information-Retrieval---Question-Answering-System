package com.ir.qa.initializer;

import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.Map.Entry;

import com.ir.qa.QueryProcessor.QueryProcessor;

public class forServlet {

	public static Map<String,Map<String,String>> start(String input) {
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		String result = "";
		// QueryProcessor.initialize();
		// while (true) {
		try {
			// QueryProcessor.query(input);
			resultMap = QueryProcessor.query(input);
			// System.out.println(resultMap);
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		return resultMap;
	}
	
	
}
