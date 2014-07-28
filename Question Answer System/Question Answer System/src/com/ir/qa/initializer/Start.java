package com.ir.qa.initializer;

import java.io.*;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.ir.qa.QueryProcessor.QueryProcessor;
import com.ir.qa.parser.WikiDocGenerator;
import com.ir.qa.parser.WikiIndexer;
import com.ir.qa.parser.WikipediaDocument;

public class Start {

	private static Thread parserThread;
	private static Thread indexerThread;
	private static Thread queryProcessorThread;
	private static boolean index = false;

	public static void main(String args[]) throws IOException {
		System.out.println("Entered main function");

		boolean query = false;

		String option = "";
		if (args[0] != null && args[0].length() > 0) {
			option = args[0];
			if (option.equalsIgnoreCase("-i")) {
				index = true;
			} else if (option.equalsIgnoreCase("-q")) {
				query = true;
			} else if (option.equalsIgnoreCase("-b")) {
				index = true;
				query = true;
			}
		} else {
			System.out
					.println("Provide option \n -i index \n -q query \n -b both");
			System.exit(1);
		}

		ConcurrentLinkedQueue<WikipediaDocument> queue = new ConcurrentLinkedQueue<WikipediaDocument>();

		if (index) {
			ParserRunner prunner = new ParserRunner(queue);
			parserThread = new Thread(prunner);
			parserThread.start();

			IndexerRunner iRunner = new IndexerRunner(queue);
			indexerThread = new Thread(iRunner);
			indexerThread.start();

			new Thread(new ParserChecker(queue)).start();

		}

		if (query) {
			QueryProcessorRunner qRuner = new QueryProcessorRunner();
			queryProcessorThread = new Thread(qRuner);
			queryProcessorThread.start();
		}
	}

	private static class ParserRunner implements Runnable {
		private Collection<WikipediaDocument> coll;
		private WikiDocGenerator wikiDoc;

		private ParserRunner(Collection<WikipediaDocument> collection) {
			this.coll = collection;
			wikiDoc = new WikiDocGenerator("files"+File.separator+"five_entries.xml", coll);
		}

		public void run() {
			long start = System.currentTimeMillis();
			System.out.println("Parsing about to start");
		wikiDoc.parse();
			wikiDoc = new WikiDocGenerator("files"+File.separator+"WikiDump_1600.xml", coll);
			System.out.println("Parsing about to start");
			wikiDoc.parse();
			wikiDoc = new WikiDocGenerator("files"+File.separator+"WikiDump_6305.xml", coll);
			System.out.println("Parsing about to start");
			wikiDoc.parse();
			wikiDoc = new WikiDocGenerator("files"+File.separator+"WikiDump_10267.xml", coll);
			System.out.println("Parsing about to start");
			wikiDoc.parse();
			wikiDoc = new WikiDocGenerator("files"+File.separator+"WikiDump_33693.xml", coll);
			System.out.println("Parsing about to start");
			wikiDoc.parse();
			System.out.println("Parser completed in ::"
					+ (System.currentTimeMillis() - start));
		}
	}

	private static class IndexerRunner implements Runnable {
		private ConcurrentLinkedQueue<WikipediaDocument> queue;
		private long sleepTime = 1500;
		private int numTries = 0;

		private IndexerRunner(ConcurrentLinkedQueue<WikipediaDocument> queue) {
			WikiIndexer.initializeServer();
			this.queue = queue;
		}

		public void run() {
			WikipediaDocument doc;
			while (!Thread.interrupted()) {
				doc = queue.poll();

				if (doc == null) {
					try {
						numTries++;
						System.out.println("Num Tries::" + numTries);
						if (numTries > 5) {

							sleepTime *= 2;
							numTries = 0;
						}

						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						System.out.println("interrupted");
						break;
					}
				} else {
					if (numTries > 0)
						numTries--;
					WikiIndexer.createIndex(doc);
					// System.out.println("Count::"+(++count));
				}
			}
			WikiIndexer.commit();
			System.out.println("Indexing completed");
			synchronized (this) {
				System.out.println("Notify");
				notify();
			}
		}
	}

	private static class ParserChecker implements Runnable {
		private ConcurrentLinkedQueue<WikipediaDocument> queue;

		private ParserChecker(ConcurrentLinkedQueue<WikipediaDocument> queue) {
			this.queue = queue;
		}

		public void run() {
			while (true) {
				if (!parserThread.isAlive() && getQueueSize(queue) == 0) {
					System.out.println("Thread is not alive");
					indexerThread.interrupt();
					break;
				} else {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static class QueryProcessorRunner implements Runnable {
		// private ConcurrentLinkedQueue<WikipediaDocument> queue;
		private QueryProcessorRunner() {
			// this.queue = queue;
		}

		public void run() {
			QueryProcessor.initialize();

			if (index) {
				synchronized (indexerThread) {
					try {
						System.out.println("System is going to wait");
						indexerThread.wait();
						System.out.println("Wait completed");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// while(true){
			try {
				// QueryProcessor.fileQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
			while (true) {
				QueryProcessor.queryInput();
			}
		}
	}

	private static int getQueueSize(
			ConcurrentLinkedQueue<WikipediaDocument> queue) {
		synchronized (queue) {
			return queue.size();
		}
	}
}
