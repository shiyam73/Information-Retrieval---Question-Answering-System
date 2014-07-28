package com.ir.qa.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class WikiIndexer {

	private static SolrServer server = null;
	private static int count = 0;
	private static int id = 0;
	private static HashMap<String, String> fieldNames = new HashMap<String, String>();
	private static List<SolrInputDocument> docList = new ArrayList<SolrInputDocument>();
	private static HashSet<String> fieldTypes = new HashSet<String>();

	public static void initializeServer() {
		String url = "http://localhost:8983/solr";
		try {
			server = new HttpSolrServer(url);
			File f = new File("files" + File.separator + "FieldTypes.txt");
			// File f = new File();

			// WikiDocGenerator wikiDoc = new WikiDocGenerator(docs);
			FileReader fr = null;
			BufferedReader br = null;

			File f1 = new File("files" + File.separator + "FieldMapping.txt");
			FileReader fr1 = null;
			BufferedReader br1 = null;

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
			if (fieldNames.isEmpty()) {
				try {
					if (f1.exists()) {
						// System.out.println("File exists");
						fr1 = new FileReader(f1);
						br1 = new BufferedReader(fr1);

						while ((line = br1.readLine()) != null) {
							line = line.toLowerCase().trim();
							fieldNames.put(line.replaceAll("_", "").trim(),
									line.trim());
						}
						// System.out.println("REading line");
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (fr1 != null) {
							fr1.close();
						}
						if (br1 != null) {
							br1.close();
						}
					} catch (Exception e) {

					}
				}
				System.out.println("fields List::" + fieldNames);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createIndex(WikipediaDocument wikiDoc) {
		try {

			SolrInputDocument doc = new SolrInputDocument();
			String content = wikiDoc.getInfoboxContent();
			// System.out.println("content::" + content);
			if (content.indexOf("|") != -1 && content.lastIndexOf("}}") != -1) {
				content = content.replace("}}", "");
				String[] fields = content.split("\\|");
				// System.out.println("Field length::" + fields.length);
				for (int j = 0; j < fields.length; j++) {
					// System.out.println("fields::" + fields[j]);
					String[] keyValue = fields[j].split("=");
					// System.out.println("Length ::" + keyValue.length);
					if (keyValue.length == 2) {

						if (fieldTypes.contains(keyValue[0].trim())) {
							// System.out.println("Key::" + keyValue[0]
							// + "Value::" + keyValue[1]);

							doc.addField(keyValue[0].trim(), keyValue[1].trim());
						} else if (fieldNames.containsKey(keyValue[0].trim())) {
							doc.addField(fieldNames.get(keyValue[0].trim()),
									keyValue[1].trim());
						}
					}
				}
				// doc.addField("text", "aaa"); server.add(docList);

				doc.addField("id", wikiDoc.getId());
				doc.addField("wikidoc_title", wikiDoc.getTitle());
				docList.add(doc);

				if (docList.size() >= 100) {
					// server.add(doc);
					count += docList.size();
					System.out.println("Commit::" + count);
					server.add(docList);
					server.commit();
					docList.clear();
				}
			}
		} catch (Exception e) {
			// System.out.println(wikiDoc.getTitle());
			e.printStackTrace();
		}
	}

	public static void commit() {
		// TODO Auto-generated method stub
		try {
			if (docList.size() != 0) {
				count += docList.size();
				server.add(docList);
				System.out.println("Final Commit::" + count);
				server.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}