package com.ir.qa.QueryProcessor;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class LinguisticProcessor {
	private static MaxentTagger tag = null;
	
	public LinguisticProcessor(){
		System.out.println(System.getProperty("server.home")+File.separator+"tagger"+File.separator+"english-left3words-distsim.tagger");
		tag=new MaxentTagger(System.getProperty("server.home")+File.separator+"tagger"+File.separator+"english-left3words-distsim.tagger");
	}
	
	public LinkedList<POS> findPOS(String question) throws IOException,ClassNotFoundException
	{
		String split[];	
		String tagged=tag.tagString(question);
		//System.out.println(tagged);
		split=tagged.split(" ");
		String[] dualString= new String[2]; 
		LinkedList<POS> posList= new LinkedList<POS>();
		
		for(int i=0;i<split.length;i++)
		{
			dualString=split[i].split("_");
			POS pos= new POS();
			pos.setPosValue(dualString[0]);
			pos.setPosType(dualString[1]);
			posList.add(pos);
			
		}
		return posList;
	}
}
