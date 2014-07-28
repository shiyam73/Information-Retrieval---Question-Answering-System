package com.ir.qa.QueryProcessor;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

import java.util.LinkedList;
import java.util.List;
import java.io.File;
import java.io.IOException;

public class NamedEntityRecognition {

	private static AbstractSequenceClassifier<CoreLabel> classifier = null;

	public NamedEntityRecognition() {
		String serializedClassifier = System.getProperty("server.home")+File.separator+"classifiers"+File.separator+"english.muc.7class.distsim.crf.ser.gz";
		System.out.println("Serialized Classifier"+serializedClassifier);
		File f = new File(serializedClassifier);
		if(f.exists()){
			System.out.println("File exists");
		}
		classifier = CRFClassifier
				.getClassifierNoExceptions(serializedClassifier);
	}

	public LinkedList<Entity> findEntity(String question) throws IOException {

		List<Triple<String, Integer, Integer>> A2 = classifier
				.classifyToCharacterOffsets(question);
		// System.out.println(A2);
		LinkedList<Entity> entityList = new LinkedList<Entity>();

		for (int i = 0; i < A2.size(); i++) {

			Entity entity = new Entity();
			Triple<String, Integer, Integer> tripleUnit = A2.get(i);
			entity.setEntityType(tripleUnit.first);
			entity.setEntityValue(question.substring(tripleUnit.second,
					tripleUnit.third));
			entityList.add(entity);
		}

		return entityList;
	}

}