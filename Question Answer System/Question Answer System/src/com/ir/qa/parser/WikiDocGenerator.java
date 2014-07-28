package com.ir.qa.parser;


import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

public class WikiDocGenerator extends Thread{

	private Collection<WikipediaDocument> docs;
	private String filename;

	public WikiDocGenerator(String fileName,Collection<WikipediaDocument> docs){
		this.filename = fileName;
		this.docs = docs;
	}
		public void parse(){
			long start = System.currentTimeMillis();
			System.out.println("Start Time ::"+start);
			System.out.println("Filename::"+filename);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser;
			try {
				saxParser = factory.newSAXParser();
				WikiHandler handler = new WikiHandler(docs);
				Reader isr = new InputStreamReader(new FileInputStream(filename),"UTF-8");
				InputSource is = new InputSource();
				is.setCharacterStream(isr);
				saxParser.parse(filename, handler);
				
				System.out.println("Size::"+docs.size());
				
				long end = System.currentTimeMillis();
				System.out.println("End Time::"+end);
				System.out.println("Total Time::"+(end - start));
				System.out.println("Count::"+handler.count);
				System.out.println("Parser :: Size::"+docs.size());

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println(filename);
				System.out.println(e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			// Default Handler to handle tag events
	}

}
