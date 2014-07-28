package com.ir.qa.QueryProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.LevenshteinTest;

public class QueryProcessorUtil {

	public static ArrayList<String> closestFields(Levenshtein levenshtein,String fieldToMatch,Collection<String> fieldNames,Map<String,String> relWords){
		ArrayList<String> fieldList = new ArrayList<String>();
		PriorityQueue<Field> priorityQueue = new PriorityQueue<Field>(5,new FieldComparator());
		for (String fieldName : fieldNames) {

			float distance = levenshtein.getSimilarity(fieldToMatch, fieldName);
			
			Field f = new Field();
			f.setFieldName(fieldName);
			f.setDistance(distance);
		//	System.out.println(fieldName+"::"+distance);
			priorityQueue.add(f);
		}

		Set<String> myset = relWords.keySet();
		Iterator<String> setIt = myset.iterator();
		while (setIt.hasNext()) {
			String next = setIt.next().toString();
			float distance = levenshtein.getSimilarity(fieldToMatch, next);

			Field f = new Field();
			f.setFieldName(relWords.get(next));
			f.setDistance(distance);
		//	System.out.println(next+"::"+relWords.get(next)+"::"+distance);
			priorityQueue.add(f);
		}
		
		for(int i=0;i<5;i++){
			String fieldName = priorityQueue.poll().getName();
			System.out.println(fieldName);
			fieldList.add(fieldName);
		}
		
		return fieldList;
	}
	
	public static class Field{
		String fieldName = null;
		float distance =0.0f;
		
		private void setFieldName(String fieldName){
			this.fieldName = fieldName;
		}
		
		private void setDistance(float distance){
			this.distance = distance;
		}
		
		public float getDistance(){
			return this.distance;
		}
		
		public String getName(){
			return fieldName;
		}
	}
	
	private static class FieldComparator implements Comparator<Field>{

		@Override
		public int compare(Field f, Field f1) {
			if(f.getDistance() > f1.getDistance()){
				if(f.getName().equalsIgnoreCase(f1.getName())){
					f1.setDistance(0);
				}
				return -1;
			}else{
				if(f.getName().equalsIgnoreCase(f1.getName())){
					f.setDistance(0);
				}
				return 1;
			}
			// TODO Auto-generated method stub
		}
	}
}
